package org.tootto.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import org.tootto.R;
import org.tootto.entity.Attachment;
import org.tootto.entity.Status;
import org.tootto.listener.LinkMovementMethodOverride;
import org.tootto.listener.StatusActionListener;
import org.tootto.ui.view.LinkHelper;
import org.tootto.ui.view.MastodonReblogButton;
import org.tootto.util.CustomEmojiHelper;
import org.tootto.util.HtmlUtils;
import org.tootto.util.ThemeUtils;
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
    private ImageButton statusReply;
    private MastodonReblogButton statusReblog;
    private SparkButton statusFavourite;
    private ConstraintLayout container;
    private ImageView mediaPreview0;
    private ImageView mediaPreview1;
    private ImageView mediaPreview2;
    private ImageView mediaPreview3;
    private ImageView mediaOverlay0;
    private ImageView mediaOverlay1;
    private ImageView mediaOverlay2;
    private ImageView mediaOverlay3;
    private TextView sensitiveMediaWarning;
    private ImageView sensitiveMediaShow;
    private TextView mediaLabel;
    private boolean reblogged;
    private boolean favourited;

    AbstractStatusBaseViewHolder(View itemView) {
        super(itemView);
        statusDisplayName = itemView.findViewById(R.id.tv_status_tooter_display_name);
        statusUserName = itemView.findViewById(R.id.tv_status_tooter_user_name);
        statusTimePassed= itemView.findViewById(R.id.tv_status_time_passed);
        statusContent = itemView.findViewById(R.id.tv_status_content);
        statusTooterAvatar = itemView.findViewById(R.id.iv_status_tooter_avatar);
        statusReply = itemView.findViewById(R.id.ibtn_status_reply);
        statusReblog = itemView.findViewById(R.id.ibtn_status_reblog);
        reblogged = false;
        favourited = false;
        statusFavourite = itemView.findViewById(R.id.ibtn_status_favourite);
        container = itemView.findViewById(R.id.layout_status_container);
        mediaPreview0 = itemView.findViewById(R.id.status_media_preview_0);
        mediaPreview1 = itemView.findViewById(R.id.status_media_preview_1);
        mediaPreview2 = itemView.findViewById(R.id.status_media_preview_2);
        mediaPreview3 = itemView.findViewById(R.id.status_media_preview_3);
        mediaOverlay0 = itemView.findViewById(R.id.status_media_overlay_0);
        mediaOverlay1 = itemView.findViewById(R.id.status_media_overlay_1);
        mediaOverlay2 = itemView.findViewById(R.id.status_media_overlay_2);
        mediaOverlay3 = itemView.findViewById(R.id.status_media_overlay_3);
        sensitiveMediaWarning = itemView.findViewById(R.id.status_sensitive_media_warning);
        sensitiveMediaShow = itemView.findViewById(R.id.status_sensitive_media_button);
        mediaLabel = itemView.findViewById(R.id.status_media_label);
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
    private void setReblogged(boolean reblogged) {
        this.reblogged = reblogged;
        statusReblog.setFlag(reblogged);
    }

    private void setFavourited(boolean favourited) {
        this.favourited = favourited;
        statusFavourite.setChecked(favourited);
    }

    private void setUpClickViews(StatusActionListener listener, String accountId) {
        statusTooterAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onViewAccount(accountId);
            }
        });
        statusReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onReply(position);
                }
            }
        });
        statusReblog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onReblog(!reblogged, position);
                }
            }
        });


        statusFavourite.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onFavourite(!favourited, position);
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {
            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {
            }
        });
//        moreButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = getAdapterPosition();
//                if (position != RecyclerView.NO_POSITION) {
//                    listener.onMore(v, position);
//                }
//            }
//        });
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

    @NonNull
    private static String getLabelTypeText(Context context, Attachment.Type type) {
        switch (type) {
            default:
            case IMAGE:
//                return context.getString(R.string.status_media_images);
                return "Images";
            case GIFV:
            case VIDEO:
//                return context.getString(R.string.status_media_video);
                return "Video";
        }
    }

    @DrawableRes
    private static int getLabelIcon(Attachment.Type type) {
        switch (type) {
            default:
            case IMAGE:
                return R.drawable.ic_photo_size_select_actual_black_24dp;
            case GIFV:
            case VIDEO:
                return R.drawable.ic_videocam_black_24dp;
        }
    }

    private void setMediaLabel(Attachment[] attachments, boolean sensitive,
                               final StatusActionListener listener) {
        if (attachments.length == 0) {
            mediaLabel.setVisibility(View.GONE);
            return;
        }
        mediaLabel.setVisibility(View.VISIBLE);

        // Set the label's text.
        Context context = itemView.getContext();
        String labelText = getLabelTypeText(context, attachments[0].type);
        if (sensitive) {
//            String sensitiveText = context.getString("敏感内容");
            String sensitiveText = "敏感内容";
            labelText += String.format(" (%s)", sensitiveText);
        }
        mediaLabel.setText(labelText);

        // Set the icon next to the label.
        int drawableId = getLabelIcon(attachments[0].type);
        Drawable drawable = AppCompatResources.getDrawable(context, drawableId);
        ThemeUtils.setTintColor(context, drawable, R.color.colorGrassGreen);
        mediaLabel.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);

        // Set the listener for the media view action.
        int n = Math.min(attachments.length, Status.MAX_MEDIA_ATTACHMENTS);
        final String[] urls = new String[n];
        for (int i = 0; i < n; i++) {
            urls[i] = attachments[i].url;
        }
        final Attachment.Type type = attachments[0].type;
        mediaLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onViewMedia(urls, 0, type, null);
            }
        });

    }

    protected abstract int getMediaPreviewHeight(Context context);

    private void setMediaPreviews(final Attachment[] attachments, boolean sensitive,
                                  final StatusActionListener listener, boolean showingContent) {
        final ImageView[] previews = {
                mediaPreview0, mediaPreview1, mediaPreview2, mediaPreview3
        };
        final ImageView[] overlays = {
                mediaOverlay0, mediaOverlay1, mediaOverlay2, mediaOverlay3
        };
        Context context = mediaPreview0.getContext();

        final int n = Math.min(attachments.length, Status.MAX_MEDIA_ATTACHMENTS);

        final String[] urls = new String[n];
        for (int i = 0; i < n; i++) {
            urls[i] = attachments[i].url;
        }

        for (int i = 0; i < n; i++) {
            String previewUrl = attachments[i].previewUrl;
            String description = attachments[i].description;

            if(TextUtils.isEmpty(description)) {
                previews[i].setContentDescription("Media");
            } else {
                previews[i].setContentDescription(description);
            }

            previews[i].setVisibility(View.VISIBLE);

            if (previewUrl == null || previewUrl.isEmpty()) {
                Glide.with(context).load(R.drawable.media_preview_unloaded_dark).into(previews[i]);
            } else {
                RequestOptions headerOptions = new RequestOptions()
                        .placeholder(R.drawable.media_preview_unloaded_dark)
                        .centerCrop();

                Glide.with(context)
                        .load(previewUrl)
                        .apply(headerOptions)
                        .into(previews[i]);
            }

            final Attachment.Type type = attachments[i].type;
            if (type == Attachment.Type.VIDEO | type == Attachment.Type.GIFV) {
                overlays[i].setVisibility(View.VISIBLE);
            } else {
                overlays[i].setVisibility(View.GONE);
            }

            if (urls[i] == null || urls[i].isEmpty()) {
                previews[i].setOnClickListener(null);
            } else {
                final int urlIndex = i;
                previews[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onViewMedia(urls, urlIndex, type, v);
                    }
                });
            }

            if(n <= 2) {
                previews[0].getLayoutParams().height = getMediaPreviewHeight(context)*2;
                previews[1].getLayoutParams().height = getMediaPreviewHeight(context)*2;
            } else {
                previews[0].getLayoutParams().height = getMediaPreviewHeight(context);
                previews[1].getLayoutParams().height = getMediaPreviewHeight(context);
                previews[2].getLayoutParams().height = getMediaPreviewHeight(context);
                previews[3].getLayoutParams().height = getMediaPreviewHeight(context);
            }
        }

        String hiddenContentText;
        if(sensitive) {
            hiddenContentText = context.getString(R.string.status_sensitive_media_template,
                    context.getString(R.string.status_sensitive_media_title),
                    context.getString(R.string.status_sensitive_media_directions));

        } else {
            hiddenContentText = context.getString(R.string.status_sensitive_media_template,
                    context.getString(R.string.status_media_hidden_title),
                    context.getString(R.string.status_sensitive_media_directions));
        }

        sensitiveMediaWarning.setText(HtmlUtils.fromHtml(hiddenContentText));

        sensitiveMediaWarning.setVisibility(showingContent ? View.GONE : View.VISIBLE);
        sensitiveMediaShow.setVisibility(showingContent ? View.VISIBLE : View.GONE);
        sensitiveMediaShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onContentHiddenChange(false, getAdapterPosition());
                }
                v.setVisibility(View.GONE);
                sensitiveMediaWarning.setVisibility(View.VISIBLE);
            }
        });
        sensitiveMediaWarning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onContentHiddenChange(true, getAdapterPosition());
                }
                v.setVisibility(View.GONE);
                sensitiveMediaShow.setVisibility(View.VISIBLE);
            }
        });


        // Hide any of the placeholder previews beyond the ones set.
        for (int i = n; i < Status.MAX_MEDIA_ATTACHMENTS; i++) {
            previews[i].setVisibility(View.GONE);
        }
    }
    private void hideSensitiveMediaWarning() {
        sensitiveMediaWarning.setVisibility(View.GONE);
        sensitiveMediaShow.setVisibility(View.GONE);
    }

    public void setUpWithStatus(StatusViewData.Concrete statusViewData, final StatusActionListener listener, boolean mediaPreviewEnabled) {
        setStatusDisplayName(statusViewData.getUserFullName());
        setStatusUserName(statusViewData.getNickname());
        setStatusTimePassed(statusViewData.getCreatedAt());
        setStatusTooterAvatar(statusViewData.getAvatar());
        setStatusContent(statusViewData.getContent(), statusViewData.getMentions(), statusViewData.getEmojis(), listener);
        setReblogged(statusViewData.isReblogged());
        setFavourited(statusViewData.isFavourited());
        setUpClickViews(listener, statusViewData.getSenderId());
        Attachment[] attachments = statusViewData.getAttachments();
        boolean sensitive = statusViewData.isSensitive();
        if (mediaPreviewEnabled) {
            setMediaPreviews(attachments, sensitive, listener, statusViewData.isShowingContent());

            if (attachments.length == 0) {
                hideSensitiveMediaWarning();
//                videoIndicator.setVisibility(View.GONE);
            }
            // Hide the unused label.
            mediaLabel.setVisibility(View.GONE);
        } else {
            setMediaLabel(attachments, sensitive, listener);
            // Hide all unused views.
            mediaPreview0.setVisibility(View.GONE);
            mediaPreview1.setVisibility(View.GONE);
            mediaPreview2.setVisibility(View.GONE);
            mediaPreview3.setVisibility(View.GONE);
            hideSensitiveMediaWarning();
//            videoIndicator.setVisibility(View.GONE);
        }
    }
}
