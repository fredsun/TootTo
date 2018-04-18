package org.tootto.fragment;

import android.content.Intent;
import android.util.Log;

public class HubFragment extends BaseFragment{
    protected void viewAccount(String id) {
        Log.i("HubFragment", "id"+id);
//        Intent intent = new Intent(getContext(), AccountActivity.class);
//        intent.putExtra("id", id);
//        startActivity(intent);
    }
}
