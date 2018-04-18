package org.tootto.util;

import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;

import org.tootto.ui.view.LinkHelper;

public class CustomURLSpan extends URLSpan {
    public CustomURLSpan(String url) {
        super(url);
    }

    private CustomURLSpan(Parcel src) {
        super(src);
    }

    public static final Creator<CustomURLSpan> CREATOR = new Creator<CustomURLSpan>() {

        @Override
        public CustomURLSpan createFromParcel(Parcel source) {
            return new CustomURLSpan(source);
        }

        @Override
        public CustomURLSpan[] newArray(int size) {
            return new CustomURLSpan[size];
        }

    };

    @Override
    public void onClick(View view) {
        LinkHelper.openLink(getURL(), view.getContext());
    }

    @Override public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
    }
}
