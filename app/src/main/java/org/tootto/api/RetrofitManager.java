package org.tootto.api;

/**
 * Created by fred on 2017/12/5.
 */

import org.tootto.util.JsonUtil;
import org.tootto.util.LogUtil;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 网络请求工具
 */
public class RetrofitManager {

    private static RetrofitManager sInstance = new RetrofitManager();
    private OkHttpClient mOkHttpClient;

    public static RetrofitManager getInstance() {
        return sInstance;
    }

    private RetrofitManager() {
    }

    /**
     * 获取ApiService
     *
     * @return 所有apiService
     */
    public GetRequest_Interface getApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fy.iciba.com/") // 设置 网络请求 Url
                .client(okhttpclient())
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();
        return retrofit.create(GetRequest_Interface.class);
    }

    /**
     * 初始化okhttpclient.
     *
     * @return okhttpClient
     */
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
