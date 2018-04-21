package org.tootto.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.tootto.activity.MainActivity;
import org.tootto.R;
import org.tootto.backinterface.BackHandlerHelper;
import org.tootto.backinterface.FragmentBackHandler;

/**
 * Created by fred on 2017/11/13.
 */

public class FirstTransFragment extends Fragment implements FragmentBackHandler {
    private static final String KIND_ARG = "kind";
    private FirstPagingFragment firstPagingFragment;
    private FirstDetailFragment firstDetailFragment;
    FirstPagingFragment.Kind kind;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        kind = FirstPagingFragment.Kind.valueOf(arguments.getString(KIND_ARG));
        View view = inflater.inflate(R.layout.fragment_trans_first, container, false);
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        firstPagingFragment = FirstPagingFragment.newInstance(kind);
        firstDetailFragment = FirstDetailFragment.newInstance();
        fragmentTransaction.add(R.id.content, firstPagingFragment,"firstPagingFragment" );
        fragmentTransaction.add(R.id.content, firstDetailFragment,"firstDetailFragment" );
        fragmentTransaction.show(firstPagingFragment).hide(firstDetailFragment).commit();

        return view;
    }

    public static FirstTransFragment newInstance(FirstPagingFragment.Kind kind){
        FirstTransFragment fragment = new FirstTransFragment();
        Bundle argument = new Bundle();
        argument.putString(KIND_ARG, kind.name());
        fragment.setArguments(argument);
        return fragment;
    }

    public void transFragment(){
        getChildFragmentManager().beginTransaction().hide(firstPagingFragment).show(firstDetailFragment).commit();

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
        firstPagingFragment.showToolBar();
        if (getActivity() instanceof MainActivity){
            ((MainActivity) getActivity()).bringViewPagerToFront();
        }
        if (null == firstDetailFragment){
            firstDetailFragment = firstDetailFragment.newInstance();
            getChildFragmentManager().beginTransaction().add(R.id.content, firstDetailFragment,"firstDetailFragment" ).commit();
        }
        getChildFragmentManager().beginTransaction().hide(firstPagingFragment).show(firstDetailFragment).commit();
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
//        getChildFragmentManager().beginTransaction().hide(firstDetailFragment).show(firstPagingFragment).commit();
        getChildFragmentManager().beginTransaction().remove(firstDetailFragment).show(firstPagingFragment).commit();
        firstDetailFragment = null;
    }

    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }
}
