package org.tootto.adapter;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.tootto.R;
import org.tootto.dao.SearchHistory;
import org.tootto.listener.SearchActionListener;

import java.util.ArrayList;

public class SearchHistoryViewHolder extends ViewHolder {
    ConstraintLayout layout_search_history;
    TextView tv_searchHistory;
    ImageView iv_searchHistoryDeliver;

    public SearchHistoryViewHolder(View itemView) {
        super(itemView);
        layout_search_history = itemView.findViewById(R.id.layout_searchHistory);
        tv_searchHistory = itemView.findViewById(R.id.tv_searchHistory);
        iv_searchHistoryDeliver = itemView.findViewById(R.id.iv_searchHistoryDeliver);
    }

    public void setTextSearchHistory(String searchHistory, SearchActionListener listener) {
        tv_searchHistory.setText(searchHistory);
        View.OnClickListener viewClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(searchHistory, getAdapterPosition());
            }
        };

        iv_searchHistoryDeliver.setOnClickListener(viewClickListener);
    }

    public void setUpWithStatus(SearchHistory searchHistoryData, SearchActionListener listener) {
        setTextSearchHistory(searchHistoryData.getHistorytext(), listener);
    }
}
