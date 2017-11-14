package org.tootto.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tootto.MainActivity;
import org.tootto.R;
import org.tootto.adapter.FirstFragmentAdapter;
import org.tootto.listener.RecyclerViewClickListener;

import java.util.ArrayList;

/**
 * Created by fred on 2017/11/13.
 */

public class FragmentFirst extends Fragment {
    RecyclerView recyclerFirstFragment;
    ArrayList<String> mList = new ArrayList<>();
    FirstFragmentAdapter mAdapter;
    LinearLayoutManager mLinearLayoutManager;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        for (int i = 0; i < 30; i++){
            mList.add("i");
        }
        mAdapter = new FirstFragmentAdapter(mList);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerFirstFragment = view.findViewById(R.id.recyclerview_first_fragment);
        recyclerFirstFragment.setAdapter(mAdapter);
        recyclerFirstFragment.setLayoutManager(mLinearLayoutManager);
        recyclerFirstFragment.addOnItemTouchListener(new RecyclerViewClickListener(getContext(), recyclerFirstFragment, new RecyclerViewClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (getParentFragment() instanceof FragmentTransFirst){
                    ((FragmentTransFirst) getParentFragment()).transFragment();
                }
                if (getActivity() instanceof MainActivity){
                    ((MainActivity) getActivity()).bringViewPagerToFront();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        return view;
    }



    public static FragmentFirst newInstance(){
        FragmentFirst FragmentFirst = new FragmentFirst();
        return FragmentFirst;
    }
}
