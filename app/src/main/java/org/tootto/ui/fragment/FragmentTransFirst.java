package org.tootto.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tootto.MainActivity;
import org.tootto.R;
import org.tootto.backinterface.BackHandlerHelper;
import org.tootto.backinterface.FragmentBackHandler;

/**
 * Created by fred on 2017/11/13.
 */

public class FragmentTransFirst extends Fragment implements FragmentBackHandler {
    private FragmentFirst fragmentFirst;
    private FragmentFirstDetail fragmentFirstDetail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trans_first, container, false);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentFirst = FragmentFirst.newInstance();
        fragmentFirstDetail = FragmentFirstDetail.newInstance();
        fragmentTransaction.add(R.id.content, fragmentFirst,"fragmentFirst" );
        fragmentTransaction.add(R.id.content, fragmentFirstDetail,"fragmentFirstDetail" );
        fragmentTransaction.show(fragmentFirst).hide(fragmentFirstDetail).commit();

        return view;
    }

    public static FragmentTransFirst newInstance(){
        FragmentTransFirst fragmentTransFirst = new FragmentTransFirst();

        return fragmentTransFirst;
    }

    public void transFragment(){
        getChildFragmentManager().beginTransaction().hide(fragmentFirst).show(fragmentFirstDetail).commit();

    }

    /*
    * @Description: 切换 ListFragment 为 DetailFragment
    *  先把toolbar从可能的 behavior 隐藏里放出来
    *  再把 MainActivity 的 TabLayout GONE 掉后再从可能的 behavior 隐藏里出来
    *  最后hide, show, commit.如果已经退出一次, 先add,commit再show,commit
    * @author: fred
    * @date: 2017/12/5
    * @attention:
    */
    public void showDetailFragment(){
        fragmentFirst.showToolBar();
        if (getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).bringViewPagerToFront();
        }
        if (null == fragmentFirstDetail){
            fragmentFirstDetail = fragmentFirstDetail.newInstance();
            getChildFragmentManager().beginTransaction().add(R.id.content, fragmentFirstDetail,"fragmentFirstDetail" ).commit();
        }
        getChildFragmentManager().beginTransaction().hide(fragmentFirst).show(fragmentFirstDetail).commit();
    }

    /*
    * @Description: 切换 DetailFragment 为 ListFragment
    *  先把TabLayout VISIBLE
    *  再hide, show, commit
    * @author: fred
    * @date: 2017/12/4
    * @attention:
    */
    public void showListFragment(){
        if (getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).bringViewPagerToBack();
        }
//        getChildFragmentManager().beginTransaction().hide(fragmentFirstDetail).show(fragmentFirst).commit();
        getChildFragmentManager().beginTransaction().remove(fragmentFirstDetail).show(fragmentFirst).commit();
        fragmentFirstDetail = null;
    }

    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }
}
