package org.tootto.api;

import android.util.Log;

import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Created by fred on 2017/12/5.
 */

public class HttpLogger implements HttpLoggingInterceptor.Logger {
    @Override
    public void log(String message) {
        Log.d("HttpLogInfo", message);
    }
}
