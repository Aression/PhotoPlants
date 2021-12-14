package com.example.photoplants.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.photoplants.beans.SearchHistory;
import com.example.photoplants.viewHolders.SearchResultViewHolder;

public class SearchResultAdapter extends ListAdapter<SearchHistory, SearchResultViewHolder> {
    private View.OnClickListener listener;

    public SearchResultAdapter(@NonNull DiffUtil.ItemCallback<SearchHistory> diffCallback, View.OnClickListener listener) {
        super(diffCallback);
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return SearchResultViewHolder.create(parent, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        SearchHistory historyRecord = getItem(position);
        holder.bind(historyRecord);
    }
    public static class StringDiff extends DiffUtil.ItemCallback<SearchHistory> {

        @Override
        public boolean areItemsTheSame(@NonNull SearchHistory oldItem, @NonNull SearchHistory newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull SearchHistory oldItem, @NonNull SearchHistory newItem) {
            return oldItem.getPlantID().equals(newItem.getPlantID());
        }
    }
}
