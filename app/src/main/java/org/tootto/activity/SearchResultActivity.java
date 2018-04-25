package org.tootto.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.tootto.R;

/**
 * Created by fred on 2018/4/25.
 */

public class SearchResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_result_activity);
        Bundle extras = getIntent().getExtras();
        if (null != extras){
            String search_text = extras.getString("search_text");
            Log.i("activity", "search_text"+search_text);
        }
    }
}
