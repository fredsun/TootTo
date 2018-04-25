package org.tootto.dao;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


import java.util.UUID;

@Entity(tableName = "search_history")
public class SearchHistory {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "historyid")
    private String id;

    @ColumnInfo(name = "historytext")
    private String historytext;

    @Ignore
    public SearchHistory(String historytext) {
        id = UUID.randomUUID().toString();
        this.historytext = historytext;
    }

    public SearchHistory(String id, String historytext) {
        this.id = id;
        this.historytext = historytext;
    }

    public String getId() {
        return id;
    }

    public String getHistorytext() {
        return historytext;
    }
}
