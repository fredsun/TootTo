package org.tootto.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Spanned;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.tootto.BuildConfig;
import org.tootto.R;
import org.tootto.adapter.SpannedTypeAdapter;
import org.tootto.network.AuthInterceptor;
import org.tootto.network.MastodonApi;
import org.tootto.util.OkHttpUtils;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fred on 2017/12/8.
 */

public abstract class BaseActivity extends AppCompatActivity {
    public MastodonApi mastodonApi;
    protected Dispatcher mastodonApiDispatcher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        redirectIfNotLoggedIn();
        createMastodonApi();

    }

    @Override
    public void onDestroy() {
        if (mastodonApiDispatcher != null) {
            mastodonApiDispatcher.cancelAll();
        }
        super.onDestroy();
    }

    /**
     * 是否需要重新登陆
     */
    protected void redirectIfNotLoggedIn() {
        SharedPreferences preferences = getPrivatePreferences();
        String domain = preferences.getString("domain", null);
        String accessToken = preferences.getString("accessToken", null);
        if (domain == null || accessToken == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    /**
     * 创建 MastodonApi
     */
    protected void createMastodonApi() {
        mastodonApiDispatcher = new Dispatcher();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Spanned.class, new SpannedTypeAdapter())
                .create();

        OkHttpClient.Builder okBuilder =
                OkHttpUtils.getCompatibleClientBuilder()
                        .addInterceptor(new AuthInterceptor(this))
                        .dispatcher(mastodonApiDispatcher);

        if (BuildConfig.DEBUG) {
            okBuilder.addInterceptor(
                    new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC));
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(getBaseUrl())
                .client(okBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        mastodonApi = retrofit.create(MastodonApi.class);
    }

    protected String getBaseUrl() {
        SharedPreferences preferences = getPrivatePreferences();
        return "https://" + preferences.getString("domain", null);
    }

    protected SharedPreferences getPrivatePreferences() {
        return getSharedPreferences(getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
    }
}
