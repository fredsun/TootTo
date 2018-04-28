package org.tootto.fragment;

import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;

import org.tootto.activity.ViewMediaActivity;
import org.tootto.activity.ViewVideoActivity;
import org.tootto.entity.Attachment;
import org.tootto.entity.Status;

import retrofit2.Call;
import retrofit2.Callback;

public class HubFragment extends BaseFragment{
    protected void viewAccount(String id) {
        Log.i("HubFragment", "id"+id);
//        Intent intent = new Intent(getContext(), AccountActivity.class);
//        intent.putExtra("id", id);
//        startActivity(intent);
    }


    protected void viewThread(Status status) {
        Log.i("HubFragment", "status"+status.id);
//        ((FirstTransFragment) getParentFragment()).showDetailFragment();
//        Intent intent = new Intent(getContext(), ViewThreadActivity.class);
//        intent.putExtra("id", status.getActionableId());
//        intent.putExtra("url", status.getActionableStatus().url);
//        startActivity(intent);
    }

    protected void viewTag(String tag) {
        Log.i("HubFragment", "tag"+tag);
//        Intent intent = new Intent(getContext(), ViewTagActivity.class);
//        intent.putExtra("hashtag", tag);
//        startActivity(intent);
    }

    public void viewMedia(String[] urls, int urlIndex, Attachment.Type type, View view) {
        switch (type) {
            case IMAGE: {
                Intent intent = new Intent(getContext(), ViewMediaActivity.class);
                intent.putExtra("urls", urls);
                intent.putExtra("urlIndex", urlIndex);
                if (view != null) {
                    String url = urls[urlIndex];
                    ViewCompat.setTransitionName(view, url);
                    ActivityOptionsCompat options =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                                    view, url);
                    startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
                break;
            }
            case GIFV:
            case VIDEO: {
                Intent intent = new Intent(getContext(), ViewVideoActivity.class);
                intent.putExtra("url", urls[urlIndex]);
                startActivity(intent);
                break;
            }
            case UNKNOWN: {
                /* Intentionally do nothing. This case is here is to handle when new attachment
                 * types are added to the API before code is added here to handle them. So, the
                 * best fallback is to just show the preview and ignore requests to view them. */
                break;
            }
        }
    }

    public void reblogWithCallback(Status status, boolean reblog, Callback<Status> callback) {
        String id = status.getActionableId();

        Call<Status> call;
        if (reblog) {
            call = mastodonApi.reblogStatus(id);
        } else {
            call = mastodonApi.unreblogStatus(id);
        }
        call.enqueue(callback);
    }
}
