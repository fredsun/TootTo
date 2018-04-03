package org.tootto.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.tootto.R;
import org.tootto.viewdata.StatusViewData;

import java.util.Date;

/**
 * Created by fred on 2018/3/31.
 */

public class AbstractStatusBaseViewHolder extends RecyclerView.ViewHolder {
    private TextView statusDisplayName;
    private TextView statusUserName;
    private TextView statusTimePassed;
    private ImageView statusTooterAvatar;
    private TextView statusContent;
    private ImageView statusAttachment;
    private ImageButton statusReply;
    private  ImageButton statusReblog;
    private ImageButton statusFavourite;

    AbstractStatusBaseViewHolder(View itemView) {
        super(itemView);
        statusDisplayName = itemView.findViewById(R.id.tv_status_tooter_display_name);
        statusUserName = itemView.findViewById(R.id.tv_status_tooter_user_name);
        statusTimePassed= itemView.findViewById(R.id.tv_status_time_passed);
        statusContent = itemView.findViewById(R.id.tv_status_content);
        statusTooterAvatar = itemView.findViewById(R.id.iv_status_tooter_avatar);
        statusAttachment = itemView.findViewById(R.id.iv_status_attachment);
        statusReply = itemView.findViewById(R.id.ibtn_status_reply);
        statusReblog = itemView.findViewById(R.id.ibtn_status_reblog);
        statusFavourite = itemView.findViewById(R.id.ibtn_status_favourite);
    }

    private void setStatusDisplayName(String name){
        statusDisplayName.setText(name);
    }

    private void setStatusUserName(String name){
        Context context = statusUserName.getContext();
        String string = context.getString(R.string.status_username_format);
        String userName = String.format(string, name);
        statusUserName.setText(userName);
    }

    private void setStatusTimePassed(Date createdTime){
        String timePassed;
        CharSequence timePassedDescription;
        if (createdTime != null){
            long then = createdTime.getTime();
            long now = System.currentTimeMillis();
            timePassedDescription = DateUtils.getRelativeTimeSpanString(then, now, DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE).toString();
            timePassed = timePassedDescription.toString();
        }else {
            timePassed = "?未知";
            timePassedDescription = "?未知";
        }
        statusTimePassed.setText(timePassed);
        statusTimePassed.setContentDescription(timePassedDescription);
    }

    private void setStatusTooterAvatar(String url){
        RequestOptions headerOptions = new RequestOptions()
                .placeholder(R.color.colorPrimary)
                .centerCrop();

        Glide.with(statusTooterAvatar.getContext())
                .load(url)
                .apply(headerOptions)
                .into(statusTooterAvatar);
    }

    public void setUpWithStatus(StatusViewData.Concrete statusViewData) {
        setStatusDisplayName(statusViewData.getUserFullName());
        setStatusUserName(statusViewData.getNickname());
        setStatusTimePassed(statusViewData.getCreatedAt());
        setStatusTooterAvatar(statusViewData.getAvatar());

    }
}
