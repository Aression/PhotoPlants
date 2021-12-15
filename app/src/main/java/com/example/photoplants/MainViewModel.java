package com.example.photoplants;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.photoplants.beans.SearchHistory;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    //搜索界面的展示数据
    public MutableLiveData<List<SearchHistory>> toBeShownInSearchResults = new MutableLiveData<>();

    //存储数据库中当前所有的history
    public MutableLiveData<List<SearchHistory>> queryHistory = new MutableLiveData<>();

}
