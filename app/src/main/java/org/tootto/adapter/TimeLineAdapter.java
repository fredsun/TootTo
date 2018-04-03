package org.tootto.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tootto.R;
import org.tootto.viewdata.StatusViewData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fred on 2018/3/22.
 */

public class TimeLineAdapter extends RecyclerView.Adapter {
    private List<StatusViewData> statuses;


    public TimeLineAdapter() {
        super();
        this.statuses = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.status_recycle_item, parent, false);
        return new StatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position < statuses.size()){
            StatusViewData statusViewData = statuses.get(position);
            if (statusViewData instanceof StatusViewData.Placeholder){

            }else {
                StatusViewHolder statusViewHolder = (StatusViewHolder) holder;
                statusViewHolder.setUpWithStatus((StatusViewData.Concrete)statusViewData);
            }
        }else {

        }
    }

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
}
