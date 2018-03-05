package org.tootto.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.trello.rxlifecycle2.components.support.RxFragment;

import org.tootto.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by fred on 2018/3/5.
 */

public class BaseFragment extends RxFragment {
    protected List<Call> callList;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        callList = new ArrayList<>();
    }

    @Override
    public void onDestroy() {
        for (Call call: callList){
            call.cancel();
        }
        super.onDestroy();
    }

    protected SharedPreferences getSharedPreferences(){
        return getContext().getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
    }
}
