package org.tootto.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.trello.rxlifecycle2.components.support.RxFragment;

import org.tootto.R;
import org.tootto.adapter.FirstFragmentAdapter;
import org.tootto.anim.TitleBehaviorAnim;
import org.tootto.backinterface.BackHandlerHelper;
import org.tootto.backinterface.FragmentBackHandler;
import org.tootto.listener.RecyclerViewClickListener;
import org.tootto.ui.view.observablescrollview.FrameInterceptLayout;
import org.tootto.ui.view.observablescrollview.ObservableRecyclerView;
import org.tootto.ui.view.observablescrollview.ObservableScrollViewCallbacks;
import org.tootto.ui.view.observablescrollview.ScrollState;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

/**
 * Created by fred on 2017/11/13.
 */

public class FirstPagingFragment extends RxFragment implements ObservableScrollViewCallbacks, FrameInterceptLayout.DispatchTouchListener, FragmentBackHandler {
    ObservableRecyclerView recyclerFirstFragment;
    ArrayList<String> mList = new ArrayList<>();
    FirstFragmentAdapter mAdapter;
    LinearLayoutManager mLinearLayoutManager;

    TitleBehaviorAnim mTitleAnim;
    boolean isAnimInit = false;
    boolean isTitleHide = false;
    Toolbar toolbarTitle;
    String tag = "FirstPagingFragment";
    private Disposable subscribe_auto;
//    int mTotalScrollY;
    int mScrollY;
    boolean isFastScroll;
    private boolean mScrolled;
    private int mSlop;


    FrameInterceptLayout intercept_layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Fragment parentFragment = getParentFragment();
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        for (int i = 0; i < 30; i++){
            mList.add("i");
        }
        mAdapter = new FirstFragmentAdapter(mList);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        intercept_layout = view.findViewById(R.id.intercept_layout);
        intercept_layout.setDispatchTouchListener(this);
//        intercept_layout.setScrollInterceptionListener(this);
        recyclerFirstFragment = view.findViewById(R.id.recyclerview_first_fragment);
//        recyclerFirstFragment.setTouchInterceptionViewGroup((ViewGroup) parentFragment.getView().findViewById(R.id.content));
//        if (parentFragment instanceof ObservableScrollViewCallbacks) {
//            recyclerFirstFragment.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentFragment);
//        }
        recyclerFirstFragment.setAdapter(mAdapter);
        recyclerFirstFragment.setLayoutManager(mLinearLayoutManager);
        recyclerFirstFragment.setScrollViewCallbacks(this);
        recyclerFirstFragment.addOnItemTouchListener(new RecyclerViewClickListener(getContext(), recyclerFirstFragment, new RecyclerViewClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (getParentFragment() instanceof FirstTransFragment){
                    ((FirstTransFragment) getParentFragment()).showDetailFragment();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        toolbarTitle = view.findViewById(R.id.detail_toolbar);
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        mSlop = vc.getScaledTouchSlop();

        return view;
    }



    public static FirstPagingFragment newInstance(){
        FirstPagingFragment FirstPagingFragment = new FirstPagingFragment();
        return FirstPagingFragment;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging, int a, int b, int oldA, int oldB) {

    }

    @Override
    public void onDownMotionEvent() {
        if (!isAnimInit){
            mTitleAnim = new TitleBehaviorAnim(toolbarTitle);
        }
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        Log.i(tag, "scrollState"+scrollState);
    }

    @Override
    public void onDownTouchEvent(MotionEvent event) {

    }


    /*
    * @Description: 外部FrameLayout监听到的touch, 效果与BottomBehavior一致
    * @author: fred
    * @date: 2017/11/29
    * @attention: offsetY在无法滑动时(到顶/底)的触摸拖动值仍会改变.
    */
    @Override
    public void onMoveTouchEvent(MotionEvent event, int offsetX, int offsetY, int offsetDownX, int offsetDownY) {
        //实时touch速度超过100, 上滑隐藏/下拉显示
        if (Math.abs(offsetDownY )> 100){
            if (isTitleHide && offsetY >0){
                mTitleAnim.show();
                isTitleHide = false;
            }

            if (!isTitleHide && offsetY <0) {
                mTitleAnim.hide();
                isTitleHide = true;
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        return BackHandlerHelper.handleBackPress(this);
    }

    public void showToolBar(){
        if (isTitleHide){
            mTitleAnim.show();
            isTitleHide = false;
        }
    }
}
