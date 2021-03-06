package org.tootto.fragment;

import android.arch.core.util.Function;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Toast;

import org.tootto.BuildConfig;
import org.tootto.R;
import org.tootto.activity.MainActivity;
import org.tootto.activity.SettingActivity;
import org.tootto.adapter.FirstFragmentAdapter;
import org.tootto.adapter.TimeLineAdapter;
import org.tootto.anim.TitleBehaviorAnim;
import org.tootto.backinterface.BackHandlerHelper;
import org.tootto.backinterface.FragmentBackHandler;
import org.tootto.entity.Attachment;
import org.tootto.entity.Status;
import org.tootto.listener.StatusActionListener;
import org.tootto.listener.TabLayoutReSelectListener;
import org.tootto.network.MastodonApi;
import org.tootto.ui.view.EndlessOnScrollListener;
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
import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fred on 2017/11/13.
 */

public class FirstPagingFragment extends HubFragment implements ObservableScrollViewCallbacks, FrameInterceptLayout.DispatchTouchListener, FragmentBackHandler, SwipeRefreshLayout.OnRefreshListener, TabLayoutReSelectListener, StatusActionListener, SharedPreferences.OnSharedPreferenceChangeListener {
    final String TAG = "FirstPagingFragment";
    private static final String KIND_ARG = "kind";
    private static final String HASHTAG_OR_ID_ARG = "hashtag_or_id";
    ObservableRecyclerView recyclerFirstFragment;
    LinearLayoutManager mLinearLayoutManager;
    private Kind kind;
    private String hashtagOrId;
    TitleBehaviorAnim mTitleAnim;
    boolean isAnimInit = false;
    boolean isTitleHide = false;
    ConstraintLayout titleView;
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
    SwipeRefreshLayout swipeRefreshLayout;
    EndlessOnScrollListener endlessOnScrollListener;

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key){
            case "allow_preview": {
                boolean enabled = sharedPreferences.getBoolean("allow_preview", true);
                timeLineAdapter.setMediaPreviewEnabled(enabled);
                fullyRefresh();
                break;
            }
            case "load_sensitive": {
                //it is ok if only newly loaded statuses are affected, no need to fully refresh
                alwaysShowSensitiveMedia = sharedPreferences.getBoolean("load_sensitive", false);
            }

        }
    }

    private void fullyRefresh() {
        timeLineAdapter.clear();
        sendFetchTimelineRequest(null, null, FetchType.TOP, -1);
    }

    public enum Kind {
        HOME,
        PUBLIC_LOCAL,
        PUBLIC_FEDERATED,
        TAG,
        USER,
        FAVOURITES
    }

    private enum FetchType{
        TOP,//第一次刷新
        MIDDLE,//通过 onLoadMore 按钮刷新
        BOTTOM
    }

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

    @Override
    public void onRefresh() {
        sendFetchTimelineRequest(null, topId, FetchType.TOP, -1);
    }

    @Override
    public void onReselected(int position) {
        if (position == 0){
            jumpToTop();
        }
    }

    private void jumpToTop() {
        mLinearLayoutManager.scrollToPosition(0);
        endlessOnScrollListener.reset();
        //TODO 仿知乎控件的bug导致此处需多做一次判断
        mTitleAnim.show();
        isTitleHide = false;
    }

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
        Bundle arguments = getArguments();
        kind = Kind.valueOf(arguments.getString(KIND_ARG));
        View view = inflater.inflate(R.layout.fragment_first, container, false);


        timeLineAdapter = new TimeLineAdapter(this);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        intercept_layout = view.findViewById(R.id.intercept_layout);
        intercept_layout.setDispatchTouchListener(this);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setProgressViewOffset(true, 50, 250);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorGrassGreen));
        swipeRefreshLayout.setOnRefreshListener(this);
//        intercept_layout.setScrollInterceptionListener(this);
        recyclerFirstFragment = view.findViewById(R.id.recyclerview_first_fragment);
//        recyclerFirstFragment.setTouchInterceptionViewGroup((ViewGroup) parentFragment.getView().findViewById(R.id.content));
//        if (parentFragment instanceof ObservableScrollViewCallbacks) {
//            recyclerFirstFragment.setScrollViewCallbacks((ObservableScrollViewCallbacks) parentFragment);
//        }
        recyclerFirstFragment.setHasFixedSize(true);
        recyclerFirstFragment.setAdapter(timeLineAdapter);
        recyclerFirstFragment.setLayoutManager(mLinearLayoutManager);
        recyclerFirstFragment.setScrollViewCallbacks(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(getContext(), R.drawable.custom_divider));
        recyclerFirstFragment.addItemDecoration(dividerItemDecoration);
        titleView = view.findViewById(R.id.title_view);
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = SearchHistoryFragment.newInstance(R.string.restart_confirm_title);
                dialogFragment.show(getActivity().getSupportFragmentManager(), "restartConfirmDialog");
            }
        });
        if (!(getActivity() instanceof  MainActivity)){
            titleView.setVisibility(View.GONE);
        }
        ViewConfiguration vc = ViewConfiguration.get(getContext());
        mSlop = vc.getScaledTouchSlop();
        topId = null;
        bottomId = null;
        if (!isAnimInit){
            mTitleAnim = new TitleBehaviorAnim(titleView);
        }
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
                getActivity());
        preferences.registerOnSharedPreferenceChangeListener(this);
        alwaysShowSensitiveMedia = preferences.getBoolean("load_sensitive", false);
        boolean mediaPreviewEnabled = preferences.getBoolean("allow_preview", true);
        timeLineAdapter.setMediaPreviewEnabled(mediaPreviewEnabled);
        recyclerFirstFragment.setAdapter(timeLineAdapter);
        return view;
    }

    private void sendFetchTimelineRequest(@Nullable String fromId, @Nullable String upToId, FetchType fetchType, int position) {
        Callback<List<Status>> callback = new Callback<List<Status>>() {
            @Override
            public void onResponse(Call<List<Status>> call, Response<List<Status>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful()){
                    String linkHeader = response.headers().get("Link");
                    onFetchTimeLineSuccess(response.body(), linkHeader, fetchType, position);
                }else {
                    onFetchTimeLineFailure(new Exception(response.message()));
                }
            }

            @Override
            public void onFailure(Call<List<Status>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                onFetchTimeLineFailure((Exception)t);
            }
        };


        Call<List<Status>> listCall = getFetchCallByRequestTimelineType(kind, hashtagOrId, fromId, upToId);
        callList.add(listCall);
        listCall.enqueue(callback);
    }

    private Call<List<Status>> getFetchCallByRequestTimelineType(Kind kind, String hashtagOrId, String fromId, String upToId) {
        MastodonApi api = mastodonApi;
        switch (kind){
            default:
            case HOME:
                return api.homeTimeline(fromId, upToId, 30);
            case PUBLIC_LOCAL:
                return api.publicTimeline(true, fromId, upToId, LOAD_AT_ONCE);
            case PUBLIC_FEDERATED:
                return api.publicTimeline(false, fromId, upToId, LOAD_AT_ONCE);
            case FAVOURITES:
                return api.favourites(fromId, upToId, LOAD_AT_ONCE);



        }
    }


    /**
     * 解析timeline/home的返回值
     * 请求头中存放有两组值 next 和 max_id(较小值), prev 和 since_id(较大值)
     * 第一次时直接放进adapter, 第二次时更新本地记录的timeline最大最小id值
     * max_id = fromId, since_id = upToId .
     * 即fromId在底部, 对应bottomId
     *   upToId在顶部, 对应topId
     * @param statuses
     * @param linkHeader
     * @param fetchType
     * @param position
     */
    private void onFetchTimeLineSuccess(List<Status> statuses, String linkHeader, FetchType fetchType, int position) {
        List<HttpHeaderLink> links = HttpHeaderLink.parse(linkHeader);
        boolean fullFetch = statuses.size() >= LOAD_AT_ONCE;
        switch (fetchType){
            case TOP: {
                HttpHeaderLink previous = HttpHeaderLink.findByRelationType(links, "prev");
                String upToId = null;
                if (previous != null) {
                    upToId = previous.uri.getQueryParameter("since_id");
                }
                updateStatus(statuses, null, upToId, fullFetch);

                break;
            }
            case MIDDLE: {
                Log.i(TAG, "MIDDLE SUCCESS");
                break;
            }
            case BOTTOM: {
                HttpHeaderLink next = HttpHeaderLink.findByRelationType(links, "next");
                String fromId = null;
                if (next != null) {
                    fromId = next.uri.getQueryParameter("max_id");
                }
                //区分下拉刷新还是第一次
                if (timeLineAdapter.getItemCount() > 2) {
                    addItems(statuses, fromId);
                } else {
                    HttpHeaderLink prev = HttpHeaderLink.findByRelationType(links, "prev");
                    String upToId = null;
                    if (prev != null) {
                        upToId = prev.uri.getQueryParameter("since_id");
                        updateStatus(statuses, fromId, upToId, fullFetch);
                    }
                }
                break;
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

    private void addItems(List<Status> newStatuses, String fromId) {
        if (ListUtils.isEmpty(newStatuses)){
            return;
        }
        int end = statuses.size();
        Status last = statuses.get(end -1).getAsRightOrNull();
        if (last != null && !findStatus(newStatuses, last.id)){
            statuses.addAll(listStatusList(newStatuses));
            List<StatusViewData> newViewDatas = statuses.getPairedCopy().subList(statuses.size() - newStatuses.size(), statuses.size());
            if (BuildConfig.DEBUG && newStatuses.size() != newViewDatas.size()) {
                String error = String.format(Locale.getDefault(),
                        "Incorrectly got statusViewData sublist." +
                                " newStatuses.size == %d newViewDatas.size == %d, statuses.size == %d",
                        newStatuses.size(), newViewDatas.size(), statuses.size());
                throw new AssertionError(error);
            }
            if (fromId != null) {
                bottomId = fromId;
            }
            timeLineAdapter.addItems(newViewDatas);
        }

    }

    private static boolean findStatus(List<Status> statuses, String id) {
        for (Status status : statuses) {
            if (status.id.equals(id)) {
                return true;
            }
        }
        return false;
    }

    private void onFetchTimeLineFailure(Exception exception) {
        Log.e(TAG, "Fetch Failure: " + exception.getMessage());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        request();
        endlessOnScrollListener = new EndlessOnScrollListener(mLinearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                FirstPagingFragment.this.onLoadMore();
            }
        };
        recyclerFirstFragment.addOnScrollListener(endlessOnScrollListener);
        if (getActivity() instanceof  MainActivity){
            ((MainActivity)getActivity()).setTabLayoutReSelectListener(this);
        }
    }

    public static FirstPagingFragment newInstance(Kind kind){
        FirstPagingFragment fragment = new FirstPagingFragment();
        Bundle argument = new Bundle();
        argument.putString(KIND_ARG, kind.name());
        fragment.setArguments(argument);
        return fragment;
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging, int a, int b, int oldA, int oldB) {

    }

    @Override
    public void onDownMotionEvent() {

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

    private @Nullable
    Pair<StatusViewData.Concrete, Integer>
    findStatusAndPosition(int position, Status status) {
        StatusViewData.Concrete statusToUpdate;
        int positionToUpdate;
        StatusViewData someOldViewData = statuses.getPairedItem(position);

        // Unlikely, but data could change between the request and response
        if ((someOldViewData instanceof StatusViewData.Placeholder) ||
                !((StatusViewData.Concrete) someOldViewData).getId().equals(status.id)) {
            // try to find the status we need to update
            int foundPos = statuses.indexOf(Either.<Placeholder, Status>right(status));
            if (foundPos < 0) return null; // okay, it's hopeless, give up
            statusToUpdate = ((StatusViewData.Concrete)
                    statuses.getPairedItem(foundPos));
            positionToUpdate = position;
        } else {
            statusToUpdate = (StatusViewData.Concrete) someOldViewData;
            positionToUpdate = position;
        }
        return new Pair<>(statusToUpdate, positionToUpdate);
    }

    private void onLoadMore(){
        sendFetchTimelineRequest(bottomId, null, FetchType.BOTTOM, -1);
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

    @Override
    public void onReply(int position) {
        Toast.makeText(getContext(), "暂时无法回复", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReblog(boolean reblog, int position) {
        final Status status = statuses.get(position).getAsRight();
        super.reblogWithCallback(status, reblog, new Callback<Status>() {
            @Override
            public void onResponse(@NonNull Call<Status> call, @NonNull Response<Status> response) {

                if (response.isSuccessful()) {
                    status.reblogged = reblog;

                    if (status.reblog != null) {
                        status.reblog.reblogged = reblog;
                    }

                    Pair<StatusViewData.Concrete, Integer> actual =
                            findStatusAndPosition(position, status);
                    if (actual == null) return;

                    StatusViewData newViewData =
                            new StatusViewData.Builder(actual.first)
                                    .setReblogged(reblog)
                                    .createStatusViewData();
                    statuses.setPairedItem(actual.second, newViewData);
                    timeLineAdapter.changeItem(actual.second, newViewData, false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Status> call, @NonNull Throwable t) {
                Log.d(TAG, "Failed to reblog status " + status.id, t);
            }
        });
    }

    @Override
    public void onFavourite(boolean favourite, int position) {
        final Status status = statuses.get(position).getAsRight();

        super.favouriteWithCallback(status, favourite, new Callback<Status>() {
            @Override
            public void onResponse(@NonNull Call<Status> call, @NonNull Response<Status> response) {

                if (response.isSuccessful()) {
                    status.favourited = favourite;

                    if (status.reblog != null) {
                        status.reblog.favourited = favourite;
                    }

                    Pair<StatusViewData.Concrete, Integer> actual =
                            findStatusAndPosition(position, status);
                    if (actual == null) return;

                    StatusViewData newViewData = new StatusViewData
                            .Builder(actual.first)
                            .setFavourited(favourite)
                            .createStatusViewData();
                    statuses.setPairedItem(actual.second, newViewData);
                    timeLineAdapter.changeItem(actual.second, newViewData, false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Status> call, @NonNull Throwable t) {
                Log.d(TAG, "Failed to favourite status " + status.id, t);
            }
        });
    }

    @Override
    public void onMore(View view, int position) {

    }

    @Override
    public void onViewMedia(String[] urls, int urlIndex, Attachment.Type type, View view) {
        super.viewMedia(urls, urlIndex, type, view);
    }

    @Override
    public void onViewThread(int position) {
        super.viewThread(statuses.get(position).getAsRight());
    }

    @Override
    public void onOpenReblog(int position) {

    }

    @Override
    public void onExpandedChange(boolean expanded, int position) {

    }

    @Override
    public void onContentHiddenChange(boolean isShowing, int position) {

    }

    @Override
    public void onLoadMore(int position) {

    }

    @Override
    public void onViewTag(String tag) {
        if (kind == Kind.TAG && hashtagOrId.equals(tag)) {
            // If already viewing a tag page, then ignore any request to view that tag again.
            return;
        }
        super.viewTag(tag);
    }

    @Override
    public void onViewAccount(String id) {
        if (kind == Kind.USER && hashtagOrId.equals(id)) {
            /* If already viewing an account page, then any requests to view that account page
             * should be ignored. */
            return;
        }
        super.viewAccount(id);
    }
}
