package org.tootto.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tootto.R;
import org.tootto.listener.StatusActionListener;
import org.tootto.viewdata.StatusViewData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fred on 2018/3/22.
 */

public class TimeLineAdapter extends RecyclerView.Adapter {
    private List<StatusViewData> statuses;
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_NORMAL = 1;
    private StatusActionListener statusActionListener;
    boolean mediaPreviewEnabled;

    public TimeLineAdapter(StatusActionListener statusActionListener) {
        super();
        this.statuses = new ArrayList<>();
        this.statusActionListener = statusActionListener;
        mediaPreviewEnabled = true;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0){
            return TYPE_HEADER;
        }
        return TYPE_NORMAL;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            default:
            case TYPE_NORMAL:
                View normal = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_recycle_item, parent, false);
                return new StatusViewHolder(normal);
            case TYPE_HEADER:
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_header_item, parent, false);
                return new StatusHeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch(getItemViewType(position)){
            case TYPE_HEADER:
                break;
            case TYPE_NORMAL:
                position = position -1;
                if (position < statuses.size()){

                    StatusViewData statusViewData = statuses.get(position);
                    if (statusViewData instanceof StatusViewData.Placeholder){

                    }else {
                        StatusViewHolder statusViewHolder = (StatusViewHolder) holder;
                        statusViewHolder.setUpWithStatus((StatusViewData.Concrete)statusViewData, statusActionListener, mediaPreviewEnabled);
                    }
                }else {

                }
                break;
        }
    }

    //TODO might need + 2
    @Override
    public int getItemCount() {
        return statuses.size()+1;
    }

    public void update(List<StatusViewData> newStatuses){
        if (newStatuses == null || newStatuses.isEmpty()){
            return;
        }
        statuses.clear();
        statuses.addAll(newStatuses);
        notifyDataSetChanged();
    }

    public void addItems(List<StatusViewData> newStatuses) {
        statuses.addAll(newStatuses);
        notifyItemRangeInserted(statuses.size(), newStatuses.size());
    }

    public void setMediaPreviewEnabled(boolean mediaPreviewEnabled) {
        this.mediaPreviewEnabled = mediaPreviewEnabled;
    }
}
