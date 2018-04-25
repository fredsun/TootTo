package org.tootto.dao;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {SearchHistory.class},version = 1)
public abstract class AppDatabase extends RoomDatabase{
    public abstract SearchHistoryDao searchHistoryDao();
}
