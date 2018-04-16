package org.tootto.fragment;

import android.arch.core.util.Function;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import org.tootto.R;
import org.tootto.adapter.FirstFragmentAdapter;
import org.tootto.adapter.TimeLineAdapter;
import org.tootto.anim.TitleBehaviorAnim;
import org.tootto.backinterface.BackHandlerHelper;
import org.tootto.backinterface.FragmentBackHandler;
import org.tootto.entity.Status;
import org.tootto.listener.RecyclerViewClickListener;
import org.tootto.network.MastodonApi;
import org.tootto.ui.view.observablescrollview.FrameInterceptLayout;
import org.tootto.ui.view.observablescrollview.ObservableRecyclerView;
import org.tootto.ui.view.observablescrollview.ObservableScrollViewCallbacks;
import org.tootto.ui.view.observablescrollview.ScrollState;
import org.tootto.util.CollectionUtil;
import org.tootto.util.Either;
import org.tootto.util.HttpHeaderLink;
import org.tootto.util.ListUtils;
import org.tootto.util.PairedList;
import org.tootto.util.ViewDataUtils;
import org.tootto.viewdata.StatusViewData;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fred on 2017/11/13.
 */

public class FirstPagingFragment extends BaseFragment implements ObservableScrollViewCallbacks, FrameInterceptLayout.DispatchTouchListener, FragmentBackHandler {
    final String TAG = "FirstPagingFragment";
    ObservableRecyclerView recyclerFirstFragment;
    ArrayList<String> mList = new ArrayList<>();
    LinearLayoutManager mLinearLayoutManager;

    TitleBehaviorAnim mTitleAnim;
    boolean isAnimInit = false;
    boolean isTitleHide = false;
    Toolbar toolbarTitle;
    String tag = "FirstPagingFragment";
    private int mSlop;
    FrameInterceptLayout intercept_layout;
    private OkHttpClient.Builder builder;
    private TimeLineAdapter timeLineAdapter;
    private static final int LOAD_AT_ONCE = 30;
    @Nullable
    private String bottomId;
    @Nullable
    private String topId;

    private boolean alwaysShowSensitiveMedia;
    private PairedList<Either<Placeholder, Status>, StatusViewData> statuses =
            new PairedList<>(new Function<Either<Placeholder, Status>, StatusViewData>() {
                @Override
                public StatusViewData apply(Either<Placeholder, Status> input) {
                    Status status = input.getAsRightOrNull();
                    if (status != null) {
                        return ViewDataUtils.statusToViewData(status, alwaysShowSensitiveMedia);
                    } else {
                        return new StatusViewData.Placeholder(false);
                    }
                }
            });

    private static final class Placeholder {
        private final static Placeholder INSTANCE = new Placeholder();

        public static Placeholder getInstance() {
            return INSTANCE;
        }

        private Placeholder() {
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        for (int i = 0; i < 30; i++){
            mList.add("i");
        }

        timeLineAdapter = new TimeLineAdapter();
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        intercept_layout = view.findViewById(R.id.intercept_layout);
        intercept_layout.setDispatchTouchListener(this);
//        intercept_layout.setScrollInterceptionListener(this);
        recyclerFirstFragment = view.findViewById(R.id.recyclerview_first_fragment);
//        recyclerFirstFragment.setTouchInterceptionViewGroup((ViewGroup) parentFragment.getView().findViewById(R.id.content));
//        if (parentFragment instanceof ObservableScrollViewCallbacks) {
//            recyclerFirstFragment.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentFragment);
//        }
        recyclerFirstFragment.setAdapter(timeLineAdapter);
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

    private void getTimeLine() {
        Callback<List<Status>> callback = new Callback<List<Status>>() {
            @Override
            public void onResponse(Call<List<Status>> call, Response<List<Status>> response) {
                if (response.isSuccessful()){
                    String linkHeader = response.headers().get("Link");
                    onFetchTimeLineSuccess(response.body(), linkHeader);
                }else {
                    onFetchTimeLineFailure(new Exception(response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Status>> call, Throwable t) {
                onFetchTimeLineFailure((Exception)t);
            }
        };
        MastodonApi api = mastodonApi;
        Call<List<Status>> listCall = api.homeTimeline(null, null, 30);
        callList.add(listCall);
        listCall.enqueue(callback);
    }

    /**
     * 解析timeline/home的返回值
     * 请求头中存放有两组值 next 和 max_id(较小值), prev 和 since_id(较大值)
     * 第一次时直接放进adapter, 第二次时更新本地记录的timeline最大最小id值
     * max_id = fromId, since_id = upToId .
     * @param statuses
     * @param linkHeader
     */
    private void onFetchTimeLineSuccess(List<Status> statuses, String linkHeader) {
        List<HttpHeaderLink> links = HttpHeaderLink.parse(linkHeader);
        boolean fullFetch = statuses.size() >= LOAD_AT_ONCE;
        HttpHeaderLink next = HttpHeaderLink.findByRelationType(links, "next");
        String fromId = null;
        if (next != null){
            fromId = next.uri.getQueryParameter("max_id");
        }
        //区分下拉刷新还是第一次
        if (timeLineAdapter.getItemCount() > 2){
            addItems(statuses, fromId);
        }else {
            HttpHeaderLink prev = HttpHeaderLink.findByRelationType(links, "prev");
            String upToId = null;
            if (prev != null){
                upToId = prev.uri.getQueryParameter("since_id");
                updateStatus(statuses, fromId, upToId, fullFetch);
            }
        }
    }

    private void updateStatus(List<Status> newStatuses, String fromId, String upToId, boolean fullFetch) {
        if (ListUtils.isEmpty(newStatuses)){
            return;
        }
        if (fromId != null){
            bottomId = fromId;
        }
        if (upToId != null){
            topId = upToId;
        }
        List<Either<Placeholder, Status>> liftedNew = listStatusList(newStatuses);
        if (statuses.isEmpty()){
            statuses.addAll(liftedNew);
        }else{
            Either<Placeholder, Status> lastOfNew = liftedNew.get(newStatuses.size() - 1);
            int index = liftedNew.indexOf(lastOfNew);
            for(int i=0; i<index; i++){
                statuses.remove(0);
            }
            int newIndex = liftedNew.indexOf(statuses.get(0));
            if (newIndex == -1){
                if (index == -1 && fullFetch){
                    liftedNew.add(Either.left(Placeholder.getInstance()));
                }
                statuses.addAll(0, liftedNew);
            }else {
                statuses.addAll(0, liftedNew.subList(0, newIndex));
            }
        }
        timeLineAdapter.update(statuses.getPairedCopy());
    }

    private void addItems(List<Status> statusList, String fromId) {

    }

    private void onFetchTimeLineFailure(Exception exception) {
        Log.e(TAG, "Fetch Failure: " + exception.getMessage());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        request();
        getTimeLine();
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

    private final Function<Status, Either<Placeholder, Status>> statusLifter =
            Either::right;

    private List<Either<Placeholder, Status>> listStatusList(List<Status> list) {
        return CollectionUtil.map(list, statusLifter);
    }

    public void request() {

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://fy.iciba.com/") // 设置 网络请求 Url
//                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
//                .build();
//
//
//        // 步骤5:创建 网络请求接口 的实例
//        GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);
//
//        //对 发送请求 进行封装
//        Call<Translation> call = request.getCall();
//
//        //步骤6:发送网络请求(异步)
//        call.enqueue(new Callback<Translation>() {
//            //请求成功时回调
//            @Override
//            public void onResponse(Call<Translation> call, Response<Translation> response) {
//                // 步骤7：处理返回的数据结果
//                response.body().show();
//            }
//
//            //请求失败时回调
//            @Override
//            public void onFailure(Call<Translation> call, Throwable throwable) {
//                System.out.println("连接失败");
//            }
//        });


//        RetrofitManager.getInstance().getApiService().getCall().enqueue(new Callback<Translation>() {
//            @Override
//            public void onResponse(Call<Translation> call, Response<Translation> response) {
//                response.body().show();
//            }
//
//            @Override
//            public void onFailure(Call<Translation> call, Throwable t) {
//                System.out.println("连接失败");
//            }
//        });

    }
}
