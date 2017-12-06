package org.tootto;

import android.app.Application;

import org.tootto.util.LogUtil;

/**
 * Created by fred on 2017/12/5.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.init();
    }
}
