package org.tootto.fragment;

import android.content.Intent;
import android.util.Log;

import org.tootto.entity.Status;

public class HubFragment extends BaseFragment{
    protected void viewAccount(String id) {
        Log.i("HubFragment", "id"+id);
//        Intent intent = new Intent(getContext(), AccountActivity.class);
//        intent.putExtra("id", id);
//        startActivity(intent);
    }


    protected void viewThread(Status status) {
        Log.i("HubFragment", "status"+status.id);
        ((FirstTransFragment) getParentFragment()).showDetailFragment();
//        Intent intent = new Intent(getContext(), ViewThreadActivity.class);
//        intent.putExtra("id", status.getActionableId());
//        intent.putExtra("url", status.getActionableStatus().url);
//        startActivity(intent);
    }
}
