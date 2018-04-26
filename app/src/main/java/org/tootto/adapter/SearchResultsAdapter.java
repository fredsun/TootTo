package org.tootto.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.tootto.R;
import org.tootto.entity.Account;
import org.tootto.entity.SearchResults;
import org.tootto.listener.LinkListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fred on 2018/4/26.
 */

public class SearchResultsAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_ACCOUNT = 0;
    private static final int VIEW_TYPE_HASHTAG = 1;

    private List<Account> accountList;
    private List<String> hashtagList;
    private LinkListener linkListener;

    public SearchResultsAdapter(LinkListener listener) {
        super();
        accountList = new ArrayList<>();
        hashtagList = new ArrayList<>();
        linkListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            default:
            case VIEW_TYPE_ACCOUNT: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.search_result_account_recycle_item, parent, false);
                return new AccountViewHolder(view);
            }
            case VIEW_TYPE_HASHTAG: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.search_result_hashtag_recycle_item, parent, false);
                return new HashtagViewHolder(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (position < accountList.size()) {
            AccountViewHolder holder = (AccountViewHolder) viewHolder;
            holder.setupWithAccount(accountList.get(position));
            holder.setupLinkListener(linkListener);
        } else {
            HashtagViewHolder holder = (HashtagViewHolder) viewHolder;
            int index = position - accountList.size();
            holder.setup(hashtagList.get(index), linkListener);
        }
    }

    @Override
    public int getItemCount() {
        return accountList.size() + hashtagList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position >= accountList.size()) {
            return VIEW_TYPE_HASHTAG;
        } else {
            return VIEW_TYPE_ACCOUNT;
        }
    }

    public void updateSearchResults(SearchResults results) {
        if (results != null) {
            if (results.accounts != null) {
                accountList.addAll(Arrays.asList(results.accounts));
            }
            if (results.hashtags != null) {
                hashtagList.addAll(Arrays.asList(results.hashtags));
            }
        } else {
            accountList.clear();
            hashtagList.clear();
        }
        notifyDataSetChanged();
    }
}
