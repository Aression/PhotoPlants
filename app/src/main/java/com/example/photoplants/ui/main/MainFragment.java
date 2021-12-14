package com.example.photoplants.ui.main;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.photoplants.MainViewModel;
import com.example.photoplants.R;
import com.example.photoplants.beans.SearchHistory;
import com.example.photoplants.databinding.MainFragmentBinding;
import com.example.photoplants.ui.searchResult.SearchResultFragment;
import com.example.photoplants.utils.AccessTokenUtil;
import com.example.photoplants.utils.Base64Util;
import com.example.photoplants.utils.FileUtil;
import com.example.photoplants.utils.GsonUtils;
import com.example.photoplants.utils.HttpUtil;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class MainFragment  extends Fragment {
    private MainViewModel mViewModel;
    private MainFragmentBinding binding;
    private ImageView welImg;
    private SearchView searchPlants;
    private Button selImg;
    private TextView totalSearchCount, copyRight;

    public static MainFragment newInstance(){
        return new MainFragment();
    }

    // constant to compare the activity result code
    int SELECT_PICTURE = 200;
    void imageChooser(){
        // create an instance of the
        // intent of the type image
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);

        // pass the constant to compare it
        // with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "选择一张植物图片"), SELECT_PICTURE);
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding = MainFragmentBinding.inflate(inflater, container, false);

        welImg = binding.welcomeImage;
        welImg.setImageResource(R.drawable.wel);

        searchPlants = binding.searchPlants;
        searchPlants.setIconifiedByDefault(false);
        searchPlants.setSubmitButtonEnabled(true);
        searchPlants.setQueryHint("输入关键词...");
        searchPlants.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchKeyWord(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        selImg = binding.selectImg;
        selImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChooser();
            }
        });

        totalSearchCount = binding.totalSearchCount;
        copyRight = binding.copyRight;
        copyRight.setText("CQU 19@ 2021.12.15完成编码");
        mViewModel.queryHistory.observe(requireActivity(), new Observer<List<SearchHistory>>() {
            @Override
            public void onChanged(List<SearchHistory> searchHistories) {
                totalSearchCount.setText("历史有效搜索总次数："+searchHistories.size());
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // compare the resultCode with the SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    Luban.with(getContext())
                            .load(selectedImageUri)
                            .ignoreBy(0)
                            .setTargetDir(String.valueOf(getContext().getCacheDir()))
                            .setCompressListener(new OnCompressListener() {
                                @Override
                                public void onStart() {
                                }

                                @SuppressLint("SetTextI18n")
                                @Override
                                public void onSuccess(File file) {
                                    Log.e(TAG, "onSuccess: compressed photo size: "+ Formatter.formatFileSize(getContext(),file.length()));
                                    Snackbar.make(requireView(), "image compress succeed", Snackbar.LENGTH_LONG)
                                            .setAction("img_compress_succeed", null).show();
                                    // 开始图片检索工作
                                    getAccessToken(file);
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(TAG, "onError: compress failed");
                                    Snackbar.make(requireView(), "image compress failed", Snackbar.LENGTH_LONG)
                                            .setAction("img_compress_failed", null).show();
                                }
                            }).launch();
                }
            }
        }
    }

    public void getAccessToken(File file){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String accessToken = AccessTokenUtil.getAccessToken();
                if(accessToken != null){
                    doSearchImage(file,accessToken);
                }
            }
        }).start();
    }

    public void doSearchImage(File file, String accessToken){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "https://aip.baidubce.com/rest/2.0/image-classify/v1/plant";
                // file读取
                String imgStr = null;
                try {
                    imgStr = Base64Util.encode(FileUtil.readFileByBytes(file.getPath()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // 生成参数
                String imgParam = null;
                try {
                    imgParam = URLEncoder.encode(imgStr,"UTF-8");
                    Log.e(TAG, "doSearchImage: encoded img size = "+Formatter.formatFileSize(getContext(),imgParam.length()));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                // 调用接口获取结果
                String result = null;
                try {
                    result = HttpUtil.post(url,accessToken,"image="+imgParam);
                    Log.e(TAG, "doSearchImage: image searchResult is: \n" + result);
                    String recognisedPlantName = "NULL";
                    //结果填入搜索框
                    JsonObject responseObject = GsonUtils.String2JsonObject(result);
                    if(responseObject.get("result") != null){
                        JsonArray plants = responseObject.get("result").getAsJsonArray();
                        recognisedPlantName = plants.get(0).getAsJsonObject()
                                .get("name").getAsString();
                    }

                    String finalRecognisedPlantName = recognisedPlantName;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            searchPlants.setQuery(finalRecognisedPlantName,false);
                            Snackbar.make(getView(),"图片识别成功！识别结果是"+ finalRecognisedPlantName,Snackbar.LENGTH_LONG)
                                    .setAction("imageRecogSuccessful",null).show();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void searchKeyWord(String submitKey){
        String url = "https://api.apishop.net/common/plantFamily/queryPlantListByKeyword?";
        String apiKey = "apiKey=7i6jGRKc7ed1ebda0d6faaa9a58a51526bb08a3857c2eab";
        String keyword = "&keyword="+submitKey;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url+apiKey+keyword).build();
                    Response response = client.newCall(request).execute();

                    assert response.body() != null;
                    final String result = response.body().string();
                    JsonObject responseObject = GsonUtils.String2JsonObject(result);
                    Log.e(TAG, "searchKeyWord: 请求成功，结果为：\n"+result);

                    List<SearchHistory> submitData = new ArrayList<>();
                    @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd-HH:mm");
                    String transformDate=simpleDateFormat.format(new Date());
                    if(responseObject.get("result")!=null){
                        JsonArray plantList = responseObject.get("result").getAsJsonObject()
                                                            .get("plantList").getAsJsonArray();
                        for (int i = 0; i < plantList.size(); i++) {
                            // 现在不加入数据库，因为还没有正式选择。
                            submitData.add(
                                    new SearchHistory(
                                            plantList.get(i).getAsJsonObject().get("name").getAsString(),
                                            plantList.get(i).getAsJsonObject().get("plantID").getAsString(),
                                            transformDate
                                    )
                            );
                        }
                        // 没有在主线程，这里只能用post
                        mViewModel.toBeShownInSearchResults.postValue(submitData);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.container,new SearchResultFragment()).commitNow();
                            }
                        });
                    }else{
                        Log.e(TAG, "run: ERROR，植物按关键名搜索失败！");
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
