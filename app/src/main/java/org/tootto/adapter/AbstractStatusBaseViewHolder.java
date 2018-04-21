package org.tootto.adapter;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.tootto.R;
import org.tootto.entity.Status;
import org.tootto.listener.LinkMovementMethodOverride;
import org.tootto.listener.StatusActionListener;
import org.tootto.ui.view.LinkHelper;
import org.tootto.ui.view.MastodonReblogButton;
import org.tootto.util.CustomEmojiHelper;
import org.tootto.viewdata.StatusViewData;

import java.util.Date;
import java.util.List;

/**
 * Created by fred on 2018/3/31.
 */

public abstract class AbstractStatusBaseViewHolder extends RecyclerView.ViewHolder {
    private TextView statusDisplayName;
    private TextView statusUserName;
    private TextView statusTimePassed;
    private ImageView statusTooterAvatar;
    private TextView statusContent;
    private ImageView statusAttachment;
    private ImageButton statusReply;
    private MastodonReblogButton statusReblog;
    private ImageButton statusFavourite;
    private ConstraintLayout container;

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
        container = itemView.findViewById(R.id.layout_status_container);
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

    private void setStatusContent(Spanned content, Status.Mention[] mentions, List<Status.Emoji> emojis, StatusActionListener listener) {
        Spanned emojifyText = CustomEmojiHelper.emojifyText(content, emojis, this.statusContent);
        LinkHelper.setClickableText(this.statusContent, emojifyText, mentions, listener);
    }

    private void setUpClickViews(StatusActionListener listener, String senderId) {
        View.OnClickListener viewThreadListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    listener.onViewThread(position);
                }
            }
        };

        statusContent.setOnTouchListener(new LinkMovementMethodOverride());
        statusContent.setOnClickListener(viewThreadListener);
        container.setOnClickListener(viewThreadListener);

//
    }

    public void setUpWithStatus(StatusViewData.Concrete statusViewData, final StatusActionListener listener, boolean mediaPreviewEnabled) {
        setStatusDisplayName(statusViewData.getUserFullName());
        setStatusUserName(statusViewData.getNickname());
        setStatusTimePassed(statusViewData.getCreatedAt());
        setStatusTooterAvatar(statusViewData.getAvatar());
        setStatusContent(statusViewData.getContent(), statusViewData.getMentions(), statusViewData.getEmojis(), listener);
        setUpClickViews(listener, statusViewData.getSenderId());

    }
}
