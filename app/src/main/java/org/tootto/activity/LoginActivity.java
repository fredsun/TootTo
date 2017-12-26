package org.tootto.activity;

import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.tootto.R;
import org.tootto.entity.AccessToken;
import org.tootto.entity.AppCredentials;
import org.tootto.network.MastodonApi;
import org.tootto.util.CustomTabsHelper;
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
    LinearLayout loginInputLayout;
    LinearLayout loginLoadingLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginInputLayout = findViewById(R.id.login_input_layout);
        loginLoadingLayout = findViewById(R.id.login_loading_layout);
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
        String redirectUri = getOauthRedirectUri();
        preferences = getSharedPreferences(
                getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        Intent intent = getIntent();
        Uri uri = intent.getData();
        if (null != uri && uri.toString().startsWith(redirectUri)){
            String code = uri.getQueryParameter("code");
            String error = uri.getQueryParameter("error");
            if (code != null){
                domain = preferences.getString("domain", null);
                clientId = preferences.getString("clientId", null);
                clientSecret = preferences.getString("clientSecret", null);
                setLoading(true);
                Callback<AccessToken> callback = new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                        Log.i(TAG, response.isSuccessful()+"");
                        if (response.isSuccessful()){
                            saveToken(response.body().accessToken);
                        }else {
                            Toast.makeText(LoginActivity.this, R.string.error_retrieving_oauth_token, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<AccessToken> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, R.string.error_retrieving_oauth_token, Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "onFailure");
                    }
                };

                getApiFor(domain).fetchOAuthToken(clientId,
                        clientSecret,
                        redirectUri,
                        code,
                        "authorization_code")
                        .enqueue(callback);

            }
        }
    }

    private void saveToken(String accessToken) {
        boolean committed = preferences.edit()
                .putString("domain", domain)
                .putString("accessToken", accessToken)
                .commit();
        if (!committed) {
            setLoading(false);
            editInstanceName.setError(getString(R.string.error_retrieving_oauth_token));
            return;
        }

        //TODO Notification

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void setLoading(boolean b) {
        if (b){
            loginInputLayout.setVisibility(View.GONE);
            loginLoadingLayout.setVisibility(View.VISIBLE);
        }else {
            loginInputLayout.setVisibility(View.VISIBLE);
            loginLoadingLayout.setVisibility(View.GONE);
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
                        Log.e(TAG, R.string.error_failed_app_registration + response.message());
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

    @Override
    protected void onStop() {
        super.onStop();
        if (domain != null) {
            preferences.edit()
                    .putString("domain", domain)
                    .putString("clientId", clientId)
                    .putString("clientSecret", clientSecret)
                    .apply();
        }
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

        if (!openInCustomTab(uri, this)) {
//            Intent viewIntent = new Intent(Intent.ACTION_VIEW, uri);
//            if (viewIntent.resolveActivity(getPackageManager()) != null) {
//                startActivity(viewIntent);
//            } else {
//                editText.setError(getString(R.string.error_no_web_browser_found));
//            }
            Intent urlIntent = new Intent(LoginActivity.this, OauthWebViewActivity.class);
            urlIntent.putExtra("oauthUrl", url);
            startActivity(urlIntent);
        }


        //TODO WebView
    }

    private static boolean openInCustomTab(Uri uri, Context context) {
        boolean lightTheme = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean("lightTheme", false);
        int toolbarColorRes;
        if (lightTheme) {
            toolbarColorRes = R.color.custom_tab_toolbar_light;
        } else {
            toolbarColorRes = R.color.custom_tab_toolbar_dark;
        }
        int toolbarColor = ContextCompat.getColor(context, toolbarColorRes);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(toolbarColor);
        CustomTabsIntent customTabsIntent = builder.build();
        try {
            String packageName = CustomTabsHelper.getPackageNameToUse(context);
            /* If we cant find a package name, it means theres no browser that supports
             * Chrome Custom Tabs installed. So, we fallback to the webview */
            if (packageName == null) {
                return false;
            } else {
                customTabsIntent.intent.setPackage(packageName);
                customTabsIntent.launchUrl(context, uri);
            }
        } catch (ActivityNotFoundException e) {
            Log.w("URLSpan", "Activity was not found for intent, " + customTabsIntent.toString());
            return false;
        }
        return true;
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
                LogUtil.i(mMessage.toString());
            }
        }
    }
}
