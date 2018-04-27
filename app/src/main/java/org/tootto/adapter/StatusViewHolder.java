package org.tootto.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.tootto.R;
import org.tootto.listener.StatusActionListener;
import org.tootto.viewdata.StatusViewData;

/**
 * Created by fred on 2018/3/31.
 */

class StatusViewHolder extends AbstractStatusBaseViewHolder{
    TextView statusReblogerName;
    ImageView ivReblogIcon;

    public StatusViewHolder(View itemView) {
        super(itemView);
        statusReblogerName = itemView.findViewById(R.id.tv_status_rebloger_name);
        ivReblogIcon = itemView.findViewById(R.id.ivReblogIcon);
    }

    @Override
    protected int getMediaPreviewHeight(Context context) {
        return context.getResources().getDimensionPixelSize(R.dimen.status_media_preview_height);
    }

    private void setReblogerName(String name){

        Context context = statusReblogerName.getContext();
        String format = context.getString(R.string.status_boosted_format);
        String boostedText = String.format(format, name);
        statusReblogerName.setText(boostedText);
        Drawable originalDrawable = ContextCompat.getDrawable(context, R.drawable.ic_action_reblog);
        Drawable.ConstantState state = originalDrawable.getConstantState();
        Drawable tintDrawable = DrawableCompat.wrap(state == null ? originalDrawable : state.newDrawable()).mutate();
        DrawableCompat.setTint(tintDrawable, context.getResources().getColor(R.color.colorIconUnselected));
        ivReblogIcon.setImageDrawable(tintDrawable);
//        statusReblogerName.setCompoundDrawablesWithIntrinsicBounds(tintDrawable, null, null, null);
    }
    private void hideReblogerName(){
        if (statusReblogerName != null){
            statusReblogerName.setVisibility(View.GONE);
            ivReblogIcon.setVisibility(View.GONE);
        }
    }

    @Override
    public void setUpWithStatus(StatusViewData.Concrete statusViewData, StatusActionListener listener, boolean mediaPreviewEnabled) {
        super.setUpWithStatus(statusViewData, listener, mediaPreviewEnabled);
        String rebloggedByUsername = statusViewData.getRebloggedByUsername();
        if (rebloggedByUsername == null){
            hideReblogerName();
        }else {
            setReblogerName(rebloggedByUsername);
        }
    }
}
