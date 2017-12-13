package org.tootto.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.tootto.R;
import org.tootto.entity.AppCredentials;
import org.tootto.network.MastodonApi;
import org.tootto.util.JsonUtil;
import org.tootto.util.LogUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by fred on 2017/12/8.
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";
    TextInputEditText editInstanceName;
    Button btnLogin;
    private String domain;
    private String clientId;
    private String clientSecret;
    private SharedPreferences preferences;
    String OAUTH_SCOPES = "read write follow";
    private OkHttpClient mOkHttpClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editInstanceName = findViewById(R.id.edit_instance_name);
        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        if (savedInstanceState != null) {
            domain = savedInstanceState.getString("domain");
            clientId = savedInstanceState.getString("clientId");
            clientSecret = savedInstanceState.getString("clientSecret");
        } else {
            domain = null;
            clientId = null;
            clientSecret = null;
        }
        preferences = getSharedPreferences(
                getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        if (null != intent){
            Log.i(TAG, "scehme" + intent.getScheme());
        }
    }

    /**
     * 验证站点, 验证成功调用账号验证
     * @param view
     */
    @Override
    public void onClick(View view) {
        domain = validateDomain(editInstanceName.getText().toString());
        String prefClientId = preferences.getString(domain + "/client_id", null);
        String prefClientSecret = preferences.getString(domain + "/client_secret", null);

        if (prefClientId != null && prefClientSecret != null) {
            clientId = prefClientId;
            clientSecret = prefClientSecret;
            redirectUserToAuthorizeAndLogin(editInstanceName);
        } else {
            Callback<AppCredentials> callback = new Callback<AppCredentials>() {
                @Override
                public void onResponse(@NonNull Call<AppCredentials> call,
                                       @NonNull Response<AppCredentials> response) {
                    if (!response.isSuccessful()) {
                        editInstanceName.setError(getString(R.string.error_failed_app_registration));
                        Log.e(TAG, "站点认证失败" + response.message());
                        return;
                    }
                    AppCredentials credentials = response.body();
                    clientId = credentials.clientId;
                    clientSecret = credentials.clientSecret;
                    preferences.edit()
                            .putString(domain + "/client_id", clientId)
                            .putString(domain + "/client_secret", clientSecret)
                            .apply();
                    redirectUserToAuthorizeAndLogin(editInstanceName);
                }

                @Override
                public void onFailure(@NonNull Call<AppCredentials> call, @NonNull Throwable t) {
                    editInstanceName.setError(getString(R.string.error_failed_app_registration));
                    Log.e(TAG, Log.getStackTraceString(t));
                }
            };

            try {
                getApiFor(domain)
                        .authenticateApp(getString(R.string.app_name), getOauthRedirectUri(),
                                OAUTH_SCOPES, getString(R.string.app_website))
                        .enqueue(callback);
            } catch (IllegalArgumentException e) {
                editInstanceName.setError(getString(R.string.error_invalid_domain));
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("domain", domain);
        outState.putString("clientId", clientId);
        outState.putString("clientSecret", clientSecret);
        super.onSaveInstanceState(outState);
    }

    /**
     * 验证站点名不包含http://等前缀
     * @param s
     * @return
     */
    @NonNull
    private static String validateDomain(String s) {
        // Strip any schemes out.
        s = s.replaceFirst("http://", "");
        s = s.replaceFirst("https://", "");
        // If a username was included (e.g. username@example.com), just take what's after the '@'.
        int at = s.lastIndexOf('@');
        if (at != -1) {
            s = s.substring(at + 1);
        }
        return s.trim();
    }

    private MastodonApi getApiFor(String domain) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://" + domain)
                .client(okhttpclient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(MastodonApi.class);
    }


    /**
     * 开webview进行githubOAuth验证
     * 回调地址 redirect_uri 为 intent
     * @param editText
     */
    private void redirectUserToAuthorizeAndLogin(EditText editText) {
        /* To authorize this app and log in it's necessary to redirect to the domain given,
         * activity_login there, and the server will redirect back to the app with its response. */
        String endpoint = MastodonApi.ENDPOINT_AUTHORIZE;
        String redirectUri = getOauthRedirectUri();
        Map<String, String> parameters = new HashMap<>();
        parameters.put("client_id", clientId);
        parameters.put("redirect_uri", redirectUri);
        parameters.put("response_type", "code");
        parameters.put("scope", OAUTH_SCOPES);
        String url = "https://" + domain + endpoint + "?" + toQueryString(parameters);
        Uri uri = Uri.parse(url);

            Intent viewIntent = new Intent(Intent.ACTION_VIEW, uri);
            if (viewIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(viewIntent);
            } else {
                editText.setError(getString(R.string.error_no_web_browser_found));
            }
    }

    /**
     * WebView回调地址
     * @return
     */
    private String getOauthRedirectUri() {
        String scheme = getString(R.string.oauth_scheme);
        String host = getString(R.string.oauth_host);
        return scheme + "://" + host + "/";
    }

    /**
     *
     * 用key-value拼请求链接
     */
    @NonNull
    private static String toQueryString(Map<String, String> parameters) {
        StringBuilder s = new StringBuilder();
        String between = "";
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            s.append(between);
            s.append(Uri.encode(entry.getKey()));
            s.append("=");
            s.append(Uri.encode(entry.getValue()));
            between = "&";
        }
        return s.toString();
    }

    private OkHttpClient okhttpclient() {
        if (mOkHttpClient == null) {
            HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            mOkHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .addNetworkInterceptor(logInterceptor)
                    .build();
        }
        return mOkHttpClient;
    }

    private class HttpLogger implements HttpLoggingInterceptor.Logger {
        private StringBuilder mMessage = new StringBuilder();

        @Override
        public void log(String message) {
            // 请求或者响应开始
            if (message.startsWith("--> POST")) {
                mMessage.setLength(0);
            }
            // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
            if ((message.startsWith("{") && message.endsWith("}"))
                    || (message.startsWith("[") && message.endsWith("]"))) {
                message = JsonUtil.formatJson(message);
            }
            mMessage.append(message.concat("\n"));
            // 请求或者响应结束，打印整条日志
            if (message.startsWith("<-- END HTTP")) {
                LogUtil.d(mMessage.toString());
            }
        }
    }
}
