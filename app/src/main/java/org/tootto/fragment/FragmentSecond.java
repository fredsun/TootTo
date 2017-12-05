package org.tootto.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tootto.R;
import org.tootto.adapter.FirstFragmentAdapter;

import java.util.ArrayList;

/**
 * Created by fred on 2017/11/13.
 */

public class FragmentSecond extends Fragment{
    private RecyclerView recyclerVelocity;
    private ArrayList<String> mList = new ArrayList<>();
    private FirstFragmentAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        for (int i = 0; i < 30; i++){
            mList.add(i+"");
        }
        mAdapter = new FirstFragmentAdapter(mList);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerVelocity = view.findViewById(R.id.recyclerview_velocity);
        recyclerVelocity.setAdapter(mAdapter);
        recyclerVelocity.setLayoutManager(mLinearLayoutManager);
        return view;
    }

    public static FragmentSecond newInstance(){
        FragmentSecond FragmentSecond = new FragmentSecond();
        return FragmentSecond;
    }


}
