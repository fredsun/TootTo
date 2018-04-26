package org.tootto.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;

import org.tootto.R;
import org.tootto.adapter.SearchResultsAdapter;
import org.tootto.entity.SearchResults;
import org.tootto.fragment.SearchHistoryFragment;
import org.tootto.listener.LinkListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by fred on 2018/4/25.
 */

public class SearchResultActivity extends BaseActivity implements LinkListener {
    RecyclerView recycleSearchResult;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView tvSearchText;
    String mSearchText;
    SearchResultsAdapter searchResultsAdapter;
    TextView tvNoResult;
    ProgressBar progressbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_activity);
        Bundle extras = getIntent().getExtras();
        if (null != extras){
            mSearchText = extras.getString("search_text");
            Log.i("activity", "search_text"+mSearchText);
        }
        recycleSearchResult = findViewById(R.id.recycleSearchResult);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        tvNoResult = findViewById(R.id.tvNoResult);
        progressbar = findViewById(R.id.progressbar);
        tvSearchText = findViewById(R.id.tvSearchText);
        tvSearchText.setText(mSearchText);
        tvSearchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchHistoryFragment searchHistoryFragment = SearchHistoryFragment.newInstance(R.string.restart_confirm_title);
                searchHistoryFragment.show(getSupportFragmentManager(), "restartConfirmDialog");
            }
        });
        recycleSearchResult.setLayoutManager(new LinearLayoutManager(this));
        recycleSearchResult.setHasFixedSize(true);
        searchResultsAdapter = new SearchResultsAdapter(this);
        recycleSearchResult.setAdapter(searchResultsAdapter);
        search(mSearchText);
    }

    private void search(String searchText) {
        clearResults();
        Callback<SearchResults> callback = new Callback<SearchResults>() {
            @Override
            public void onResponse(@NonNull Call<SearchResults> call, @NonNull Response<SearchResults> response) {
                if (response.isSuccessful()) {
                    SearchResults results = response.body();
                    if (results.accounts != null && results.accounts.length > 0 || results.hashtags != null && results.hashtags.length > 0) {
                        searchResultsAdapter.updateSearchResults(results);
                        hideFeedback();
                    } else {
                        displayNoResults();
                    }
                } else {
                    onSearchFailure();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SearchResults> call, @NonNull Throwable t) {
                onSearchFailure();
            }
        };
        mastodonApi.search(searchText, false)
                .enqueue(callback);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();
        if (null != extras){
            mSearchText = extras.getString("search_text");
            Log.i("activity", "search_text"+mSearchText);
            search(mSearchText);
            tvSearchText.setText(mSearchText);
        }
    }

    private void clearResults() {
        searchResultsAdapter.updateSearchResults(null);
        progressbar.setVisibility(View.VISIBLE);
        tvNoResult.setVisibility(View.GONE);
    }

    private void hideFeedback() {
        progressbar.setVisibility(View.GONE);
        tvNoResult.setVisibility(View.GONE);
    }

    private void onSearchFailure() {
        displayNoResults();
    }

    private void displayNoResults() {
        progressbar.setVisibility(View.GONE);
        tvNoResult.setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewTag(String tag) {
        Log.i("activity", "打开tag页面");
    }

    @Override
    public void onViewAccount(String id) {
        Log.i("activity", "打开account页面");
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorGrassGreen), 0);
    }
}
