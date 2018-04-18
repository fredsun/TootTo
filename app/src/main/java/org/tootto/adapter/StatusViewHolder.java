package org.tootto.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.tootto.R;
import org.tootto.listener.StatusActionListener;
import org.tootto.viewdata.StatusViewData;

/**
 * Created by fred on 2018/3/31.
 */

class StatusViewHolder extends AbstractStatusBaseViewHolder{
    TextView statusReblogerName;

    public StatusViewHolder(View itemView) {
        super(itemView);
        statusReblogerName = itemView.findViewById(R.id.tv_status_rebloger_name);
    }

    private void setReblogerName(String name){
        statusReblogerName.setText(name);
    }

    @Override
    public void setUpWithStatus(StatusViewData.Concrete statusViewData, StatusActionListener listener, boolean mediaPreviewEnabled) {
        super.setUpWithStatus(statusViewData, listener, mediaPreviewEnabled);
    }
}
