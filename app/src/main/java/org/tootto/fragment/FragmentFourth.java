package org.tootto.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tootto.R;

/**
 * Created by fred on 2017/11/13.
 */

public class FragmentFourth extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fourth, container, false);
        return view;
    }

    public static FragmentFourth newInstance(){
        FragmentFourth FragmentFourth = new FragmentFourth();
        return FragmentFourth;
    }
}
