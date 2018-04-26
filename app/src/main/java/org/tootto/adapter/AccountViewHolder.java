package org.tootto.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.tootto.R;
import org.tootto.entity.Account;
import org.tootto.listener.AccountActionListener;
import org.tootto.listener.LinkListener;
import org.tootto.ui.view.GlideRoundTransform;

class AccountViewHolder extends RecyclerView.ViewHolder {
    private View container;
    private TextView username;
    private TextView displayName;
    private ImageView avatar;
    private String accountId;

    AccountViewHolder(View itemView) {
        super(itemView);
        container = itemView.findViewById(R.id.layoutContainer);
        username = itemView.findViewById(R.id.tvUserName);
        displayName = itemView.findViewById(R.id.tvDisplayName);
        avatar = itemView.findViewById(R.id.ivAvatar);
    }

    void setupWithAccount(Account account) {
        accountId = account.id;
        String format = username.getContext().getString(R.string.status_username_format);
        String formattedUsername = String.format(format, account.username);
        username.setText(formattedUsername);
        displayName.setText(account.getDisplayName());
        Context context = avatar.getContext();
        RequestOptions avatarOptions = new RequestOptions()
                .placeholder(R.drawable.avatar_default)
                .error(R.drawable.avatar_default)
                .fallback(R.drawable.avatar_default)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .dontAnimate()
                .transform(new GlideRoundTransform(context, 5));
        Glide.with(context)
                .load(account.avatar)
                .apply(avatarOptions)
                .into(avatar);
    }

    void setupActionListener(final AccountActionListener listener) {
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onViewAccount(accountId);
            }
        });
    }

    void setupLinkListener(final LinkListener listener) {
        container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onViewAccount(accountId);
            }
        });
    }
}