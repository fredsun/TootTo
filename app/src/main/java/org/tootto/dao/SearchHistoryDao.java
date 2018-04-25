package org.tootto.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Maybe;

@Dao
public interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSearchHistory(SearchHistory searchHistories);

    @Delete
    int deleteHistory(SearchHistory searchHistories);

    @Update
    int updateSearchHistory(SearchHistory searchHistories);

    @Query("SELECT * FROM search_history")
    Flowable<List<SearchHistory>> queryAll();

    @Query("SELECT * FROM search_history")
    List<SearchHistory> queryAllList();

    @Query("SELECT * FROM search_history WHERE historytext = :text")
    List<SearchHistory> queryText(String text);

    @Query("DELETE FROM search_history WHERE historytext = :text")
    int deleteText(String text);

}
