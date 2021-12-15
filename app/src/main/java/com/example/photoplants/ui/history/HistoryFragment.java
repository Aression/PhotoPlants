package com.example.photoplants.ui.history;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoplants.MainViewModel;
import com.example.photoplants.R;
import com.example.photoplants.adapters.SearchResultAdapter;
import com.example.photoplants.beans.SearchHistory;
import com.example.photoplants.databinding.HistoryFragmentBinding;
import com.example.photoplants.ui.details.DetailsFragment;
import com.example.photoplants.ui.main.MainFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryFragment extends Fragment{

    private MainViewModel mViewModel;
    private HistoryFragmentBinding binding;
    private FloatingActionButton backToMain;

    private RecyclerView history;
    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.showDetails){
                SearchHistory history = (SearchHistory) view.getTag();

                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd-HH:mm");
                String transformDate=simpleDateFormat.format(new Date());
                SearchHistory newHistory = new SearchHistory(
                        history.getPlantName(),history.getPlantID(),transformDate
                );
                newHistory.save();
                List<SearchHistory> currAllHistories = SearchHistory.listAll(SearchHistory.class);
                mViewModel.queryHistory.setValue(currAllHistories);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, DetailsFragment.newInstance(history.getPlantID(),2)).commitNow();
                    }
                });
            }
        }
    };
    private SearchResultAdapter adapter = new SearchResultAdapter(new SearchResultAdapter.StringDiff(),listener);

    public static HistoryFragment newInstance(){
        return new HistoryFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding = HistoryFragmentBinding.inflate(inflater, container, false);

        backToMain = binding.backToMain;
        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container,new MainFragment()).commitNow();
            }
        });

        history = binding.historyRecv;
        history.setAdapter(adapter);
        history.setLayoutManager(new LinearLayoutManager(getActivity()));

        mViewModel.queryHistory.observe(requireActivity()
                , searchHistories -> adapter.submitList(searchHistories));

        return binding.getRoot();
    }
}
