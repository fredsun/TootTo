package org.tootto.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.tootto.R;
import org.tootto.listener.LinkListener;

/**
 * Created by fred on 2018/4/26.
 */

public  class HashtagViewHolder extends RecyclerView.ViewHolder {
    private TextView hashtag;

    HashtagViewHolder(View itemView) {
        super(itemView);
        hashtag = itemView.findViewById(R.id.tvHashtag);
    }

    void setup(final String tag, final LinkListener listener) {
        hashtag.setText(String.format("#%s", tag));
        hashtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onViewTag(tag);
            }
        });
    }
}
