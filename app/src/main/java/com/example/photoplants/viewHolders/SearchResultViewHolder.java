package com.example.photoplants.viewHolders;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.photoplants.beans.SearchHistory;
import com.example.photoplants.R;

public class SearchResultViewHolder extends RecyclerView.ViewHolder{
    private Context context;

    private TextView searchDate, plantName;
    private ImageView detailsIcon;

    private SearchHistory searchHistory;

    public SearchResultViewHolder(@NonNull View itemView, View.OnClickListener listener) {
        super(itemView);
        context = itemView.getContext();

        searchDate = itemView.findViewById(R.id.searchDate);
        plantName = itemView.findViewById(R.id.plantName);

        detailsIcon = itemView.findViewById(R.id.showDetails);
        detailsIcon.setOnClickListener(listener);

    }

    public void bind(SearchHistory searchHistory){
        this.searchHistory = searchHistory;
        detailsIcon.setTag(searchHistory);

        searchDate.setText(searchHistory.getSearchTime());
        plantName.setText(searchHistory.getPlantName());
    }

    public static SearchResultViewHolder create(ViewGroup parent, View.OnClickListener listener){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_recv, parent, false);
        return new SearchResultViewHolder(view, listener);
    }
}
