package org.tootto;

import android.app.Application;
import android.arch.persistence.room.Room;

import org.tootto.dao.AppDatabase;
import org.tootto.util.LogUtil;

/**
 * Created by fred on 2017/12/5.
 */

public class App extends Application {
    private static AppDatabase db;

    public static AppDatabase getDB() {
        return db;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.init();
        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "TootTo").build();

    }
}
