package com.example.photoplants.adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.photoplants.beans.SearchHistory;
import com.example.photoplants.viewHolders.SearchResultViewHolder;

// 用于展示搜索结果和历史记录的adapter
public class SearchResultAdapter extends ListAdapter<SearchHistory, SearchResultViewHolder> {

    // 传入的listener，规定了点击条目后的响应事件
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

    // 根据字段判断item的异同
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
