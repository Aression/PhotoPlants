package com.example.photoplants.ui.details;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.photoplants.R;
import com.example.photoplants.databinding.DetailedInfoFragmentBinding;
import com.example.photoplants.ui.history.HistoryFragment;
import com.example.photoplants.ui.searchResult.SearchResultFragment;
import com.example.photoplants.utils.GsonUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetailsFragment extends Fragment {
    private String plantID;
    private DetailedInfoFragmentBinding binding;

    private ImageView plantPhoto;
    private TextView plantName,rawDescription,care, featureView,manyNames;
    private FloatingActionButton backtomain;

    //标识来源以调整跳转逻辑。
    //比如如果来源是search result，则点击backtomain的时候就跳转回对应界面。其他情况同理。
    //from的定义：1表示来源于SearchResultFragment, 2表示来源于HistoryFragment
    private int from;

    public static DetailsFragment newInstance(String plantID, int from){
        return new DetailsFragment(plantID, from);
    }

    public DetailsFragment(String plantID, int from){
        this.plantID = plantID;
        this.from = from;
    }

    //格式化所用到的函数
    public String rev(String ori){
        if(ori.equals("")) return "NULL";
        else return ori;
    }

    //新建子线程，解析HTML，加载网络图片
    public void loadImg(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //获取html代码
                    String url = (String) plantPhoto.getTag();
                    Document doc = Jsoup.parse(new URL(url),50000);

                    //从html中解析出图片url
                    Element jpgs = doc.selectFirst("img[src$=.jpg]");
                    StringBuilder URL = new StringBuilder(jpgs.attr("src"));
                    Log.e(TAG, "run: 解析到的图片地址："+URL.toString());

                    //在主线程上加载图片
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //glide设置图片
                            URL.insert(4,"s");
                            Glide.with(requireContext())
                                    .load(URL.toString())
                                    .placeholder(R.drawable.ic_baseline_cloud_download_24)
                                    .error(R.drawable.ic_baseline_clear_24)
                                    .into(plantPhoto);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public void queryAndSet(){
        // 调取api获取详细信息
        String url = "https://api.apishop.net/common/plantFamily/queryPlantInfo?";
        String apiKey = "apiKey=7i6jGRKc7ed1ebda0d6faaa9a58a51526bb08a3857c2eab";
        String id = "&plantID="+plantID;

        Log.e(TAG, "queryAndSet running: "+plantID);
        new Thread(new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                try{
                    //开启网络连接并获得响应
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(url+apiKey+id).build();
                    Response response = client.newCall(request).execute();

                    //解析响应json
                    assert response.body() != null;
                    final String result = response.body().string();
                    JsonObject responseObject = GsonUtils.String2JsonObject(result);
                    Log.e(TAG, "searchKeyWord: 请求成功，结果为：\n"+result);

                    //处理文本
                    if(responseObject.get("result")!=null) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                JsonObject details = responseObject.get("result").getAsJsonObject();

                                //照养知识，简介，特性
                                String careKnowledge = rev(details.get("careKnowledge").getAsString()).replaceAll("\\s+", " ");
                                String des = rev(details.get("des").getAsString()).replaceAll("\\s+", " ");
                                String feature = rev(details.get("feature").getAsString()).replaceAll("\\s+", " ");
                                //英文名，拉丁名，学名
                                String engName = rev(details.get("engName").getAsString());
                                String latinName = rev(details.get("latinName").getAsString());
                                String botanicalName = rev(details.get("botanicalName").getAsString());

                                //分类学名称
                                List<String> strCollection = new ArrayList<>();
                                strCollection.add(rev(details.get("kingdom").getAsString()));
                                strCollection.add(rev(details.get("phylum").getAsString()));
                                strCollection.add(rev(details.get("class").getAsString()));
                                strCollection.add(rev(details.get("order").getAsString()));
                                strCollection.add(rev(details.get("family").getAsString()));
                                strCollection.add(rev(details.get("genus").getAsString()));
                                strCollection.add(rev(details.get("species").getAsString()));

                                //图片所在网址设为tag，稍后在子线程内进行加载操作。
                                StringBuilder URL = new StringBuilder(details.get("imageURL").getAsJsonArray().get(0).getAsString());
                                URL.insert(4,"s");
                                plantPhoto.setTag(URL.toString());
                                loadImg();

                                //设置文本
                                plantName.setText(rev(details.get("name").getAsString()));
                                rawDescription.setText(rev(des));
                                featureView.setText(rev(feature));
                                care.setText(rev(careKnowledge));

                                //处理空缺现象
                                String [] clazzs = {"界","门","纲","目","科","属","种"};
                                StringBuilder names = new StringBuilder("英文名：" + engName + "\n" +
                                                                        "拉丁名：" + latinName + "\n" +
                                                                        "学名：" + botanicalName + "\n");
                                for (int i = 0; i < strCollection.size(); i++) {
                                    if(!strCollection.get(i).equals("NULL")){
                                        names.append(clazzs[i]).append(": ").append(strCollection.get(i)).append("\n");
                                    }
                                }

                                //设置分类学名称
                                manyNames.setText(names.toString());
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DetailedInfoFragmentBinding.inflate(inflater, container, false);

        plantPhoto = binding.plantPhoto;
        plantName = binding.PlantName;
        rawDescription = binding.rawDescription;
        care = binding.careKnowledge;
        featureView = binding.feature;
        manyNames = binding.manyNames;

        backtomain = binding.backToMain2;
        backtomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(from == 1){
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, SearchResultFragment.newInstance()).commitNow();
                }
                else if(from == 2){
                    requireActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, HistoryFragment.newInstance()).commitNow();
                }
            }
        });

        queryAndSet();
        return binding.getRoot();
    }
}
