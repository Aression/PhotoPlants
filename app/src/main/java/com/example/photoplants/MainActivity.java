package com.example.photoplants;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.photoplants.beans.SearchHistory;
import com.example.photoplants.databinding.MainActivityBinding;
import com.example.photoplants.ui.history.HistoryFragment;
import com.example.photoplants.ui.main.MainFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.orm.SugarContext;

public class MainActivity extends AppCompatActivity {

    private MainActivityBinding binding;
    private MainViewModel viewModel;

    private FloatingActionButton historyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SugarContext.init(this);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.queryHistory.setValue(SearchHistory.listAll(SearchHistory.class));

        binding = MainActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        historyButton = binding.ViewHistory;

        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, HistoryFragment.newInstance())
                        .commitNow();
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}