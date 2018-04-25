package org.tootto.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tootto.R;
import org.tootto.dao.SearchHistory;
import org.tootto.listener.SearchActionListener;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter {
    private List<SearchHistory> data;
    SearchActionListener searchActionListener;

    public void insertData(int position, SearchHistory searchHistory){
        data.add(position, searchHistory);
    }

    public void removeData(int position){
        data.remove(position);
    }

    public SearchHistoryAdapter(SearchActionListener searchActionListener) {
        this.searchActionListener = searchActionListener;
        data = new ArrayList<>();
    }

    public void setData(List<SearchHistory> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public List<SearchHistory> getData() {
        return data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_history_recycle_item, parent, false);
        return new SearchHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SearchHistory searchHistory = data.get(position);
        if (holder instanceof SearchHistoryViewHolder){
            SearchHistoryViewHolder searchHistoryViewHolder = (SearchHistoryViewHolder) holder;
            searchHistoryViewHolder.setUpWithStatus(searchHistory, searchActionListener);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

}
