package org.tootto.ui.fragment;

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

import org.tootto.MainActivity;
import org.tootto.R;
import org.tootto.adapter.FirstFragmentAdapter;
import org.tootto.anim.TitleBehaviorAnim;
import org.tootto.listener.RecyclerViewClickListener;
import org.tootto.ui.fragment.view.observablescrollview.FrameInterceptLayout;
import org.tootto.ui.fragment.view.observablescrollview.ObservableRecyclerView;
import org.tootto.ui.fragment.view.observablescrollview.ObservableScrollViewCallbacks;
import org.tootto.ui.fragment.view.observablescrollview.ScrollState;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;

/**
 * Created by fred on 2017/11/13.
 */

public class FragmentFirst extends RxFragment implements ObservableScrollViewCallbacks, FrameInterceptLayout.DispatchTouchListener {
    ObservableRecyclerView recyclerFirstFragment;
    ArrayList<String> mList = new ArrayList<>();
    FirstFragmentAdapter mAdapter;
    LinearLayoutManager mLinearLayoutManager;

    TitleBehaviorAnim mTitleAnim;
    boolean isAnimInit = false;
    boolean isTitleHide = false;
    Toolbar toolbarTitle;
    String tag = "FragmentFirst";
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

        toolbarTitle = view.findViewById(R.id.detail_toolbar);
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        mSlop = vc.getScaledTouchSlop();

        return view;
    }



    public static FragmentFirst newInstance(){
        FragmentFirst FragmentFirst = new FragmentFirst();
        return FragmentFirst;
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

//        if (Math.abs(offsetY) > 80){
//            if (isTitleHide && offsetY >0){
//                Log.i(tag, "-1"+!recyclerFirstFragment.canScrollVertically(-1) );
//                Log.i(tag, "1"+!recyclerFirstFragment.canScrollVertically(1) );
//                Log.i(tag, "scrollShow");
//                mTitleAnim.show();
//                isTitleHide = false;
//            }
//
//            if (!isTitleHide && offsetY <0){
//                Log.i(tag, "-1"+!recyclerFirstFragment.canScrollVertically(-1) );
//                Log.i(tag, "1"+!recyclerFirstFragment.canScrollVertically(1) );
//                Log.i(tag, "scrollHide");
//                mTitleAnim.hide();
//                isTitleHide = true;
//            }
//
//        }
    }



//    @Override
//    public boolean f(MotionEvent ev, boolean moving, float diffX, float diffY) {
////        Log.i(tag, "diffX"+diffX);
//        Log.i(tag, "diffY"+diffY);
//        if (!mScrolled && mSlop < Math.abs(diffX) && Math.abs(diffY) < Math.abs(diffX)) {
//            // Horizontal scroll is maybe handled by ViewPager
//            return false;
//        }
//
//        Scrollable scrollable = getCurrentScrollable();
//        if (scrollable == null) {
//            mScrolled = false;
//            return false;
//        }
//
//        // If interceptionLayout can move, it should intercept.
//        // And once it begins to move, horizontal scroll shouldn't work any longer.
//        int toolbarHeight = toolbarTitle.getHeight();
//        int translationY = (int) intercept_layout.getTranslationY();
//        boolean scrollingUp = 0 < diffY;
//        boolean scrollingDown = diffY < 0;
//        if (scrollingUp) {
//            if (translationY < 0) {
//                mScrolled = true;
//                return true;
//            }
//        } else if (scrollingDown) {
//            if (-toolbarHeight < translationY) {
//                mScrolled = true;
//                return true;
//            }
//        }
//        mScrolled = false;
//        return false;
//    }
//
//    @Override
//    public void onDownMotionEvent(MotionEvent ev) {
//    }
//
//    @Override
//    public void onMoveMotionEvent(MotionEvent ev, float diffX, float diffY) {
//        float translationY = ScrollUtils.getFloat(intercept_layout.getTranslationY() + diffY, -toolbarTitle.getHeight(), 0);
//        intercept_layout.setTranslationY(translationY);
//        Log.i(tag,"translationY: "+translationY);
//        if (translationY < 0) {
//            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) intercept_layout.getLayoutParams();
//            lp.height = (int) (-translationY + getScreenHeight());
//            intercept_layout.requestLayout();
//        }
//    }
//
//    @Override
//    public void onUpOrCancelMotionEvent(MotionEvent ev) {
//        mScrolled = false;
//        Log.i(tag, "changeToolBar");
//    }
//
//    protected int getScreenHeight() {
//        return getActivity().findViewById(android.R.id.content).getHeight();
//    }
//
//    private Scrollable getCurrentScrollable() {
//        Fragment fragment = this;
//        if (fragment == null) {
//            return null;
//        }
//        View view = fragment.getView();
//        if (view == null) {
//            return null;
//        }
//        return (Scrollable) view.findViewById(R.id.recyclerview_first_fragment);
//    }

//    @Override
//    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
//        mScrollY = scrollY;
////        Log.i(tag, "scrollY"+scrollY);
//
//
//
//    }
//
//    @Override
//    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging, int a, int b, int oldA, int oldB) {
//
//    }
//
//    @Override
//    public void onDownMotionEvent() {
//        if (!isAnimInit){
//            mTitleAnim = new TitleBehaviorAnim(toolbarTitle);
//            isAnimInit = true;
//        }
////        //每隔300ms
////        subscribe_auto = Observable.interval( 300, TimeUnit.MILLISECONDS)
////                .compose(this.<Long>bindToLifecycle())
////                .observeOn(Schedulers.computation())
////                .subscribe(new Consumer<Long>() {
////                    @Override
////                    public void accept(Long aLong) throws Exception {
////                        Log.i(tag,"mscroll"+(mScrollY));
////                        Log.i(tag,"mScrollYRecord"+(mScrollYRecord));
////                        Log.i(tag,"mscroll - mscrollBefore"+(mScrollY - mScrollYRecord));
////                        if ((mScrollY - mScrollYRecord)>30){
////                            isFastScroll = true;
//////                            Log.i(tag, "isFastScroll"+isFastScroll);
////                        }
////                        mScrollYRecord = mScrollY;
////
////                    }
////                });
//    }
//
//    @Override
//    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
////        subscribe_auto.dispose();
//        isFastScroll = false;
//    }



}
