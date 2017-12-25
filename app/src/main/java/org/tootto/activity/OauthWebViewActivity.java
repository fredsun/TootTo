package org.tootto.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.tootto.R;

/**
 * Created by fred on 2017/12/20.
 */

public class OauthWebViewActivity extends AppCompatActivity {
    String TAG = "OauthWebViewActivity";
    WebView oauthWeb;
    ProgressBar oauthProgress;
    TextView oauthPromptText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Intent intent = getIntent();
        String url = intent.getStringExtra("oauthUrl");
//        String url = "https://mao.daizhige.org/oauth/authorize?scope=read%20write%20follow&response_type=code&client_id=52b57339ec66c009961d6d5f983d44c5d75c3a9af5d3dd745fa0bf554f52f4f3&redirect_uri=oauth2redirect%3A%2F%2Forg.tootto%2F";
        oauthProgress = findViewById(R.id.oauth_progress);
        oauthPromptText = findViewById(R.id.oauth_prompt_text);
        oauthWeb = findViewById(R.id.oauth_web);
        oauthWeb.loadUrl(url);
        oauthWeb.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.i(TAG, "onPageFinished");
                //返回"?code="表示确认授权, 但是因为redirect_uri的问题, 必定error, 所以隐藏WebView
                if (url.contains("?code=")){
                    view.setVisibility(View.GONE);
                    Intent loginSuccessIntent = new Intent(OauthWebViewActivity.this, LoginActivity.class);
                    loginSuccessIntent.setData(Uri.parse(url));
                    startActivity(loginSuccessIntent);
                    finish();
                }else if (url.contains("error=access_denied")){
                    //拒绝授权
                    view.setVisibility(View.GONE);
                    Toast.makeText(OauthWebViewActivity.this, "你拒绝了授权= =",Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "error");
                    Intent loginFailIntent = new Intent(OauthWebViewActivity.this, LoginActivity.class);
                    //不传uri
                    startActivity(loginFailIntent);
                    finish();
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                //TODO WebView load local HTML.
            }

        });

        oauthWeb.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        oauthWeb.setWebChromeClient(new webclient());

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && oauthWeb.canGoBack()){
            oauthWeb.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class webclient extends WebChromeClient{
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100){
                oauthProgress.setVisibility(View.GONE);
                oauthPromptText.setText("浏览器");
            }else {
                oauthProgress.setProgress(newProgress);
                oauthPromptText.setText("正在努力加载中...");
            }
        }
    }
}


