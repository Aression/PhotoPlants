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

    public MutableLiveData<List<SearchHistory>> toBeShownInSearchResults = new MutableLiveData<>();

    public MutableLiveData<List<SearchHistory>> queryHistory = new MutableLiveData<>();

}
