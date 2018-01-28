package org.tootto.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.tootto.R;
import org.tootto.backinterface.BackHandlerHelper;
import org.tootto.behavior.BottomBehavior;
import org.tootto.entity.Account;
import org.tootto.fragment.EditDialogFragment;
import org.tootto.fragment.FragmentSecond;
import org.tootto.fragment.FirstTransFragment;
import org.tootto.ui.view.NonSwipeableViewPager;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements BottomBehavior.onCanScrollCallback, NavigationView.OnNavigationItemSelectedListener,EditDialogFragment.EditDialogListener {
    String TAG = "MainActivity";
    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    TabLayout mainTab;
    NonSwipeableViewPager mainPager;
    boolean canScroll = true;
    private BottomBehavior mBottomBehavior;
    private SharedPreferences preferences;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.navigation_view);
        fragmentList.add(FirstTransFragment.newInstance());
        fragmentList.add(FragmentSecond.newInstance());
        fragmentList.add(FirstTransFragment.newInstance());
        fragmentList.add(FirstTransFragment.newInstance());

        getUserAvatar();

        mainPager = findViewById(R.id.main_pager);
        mainPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                switch (position){
                    case 0:
                        return "0";
                    case 1:
                        return "1";
                    case 2:
                        return "2";
                    case 3:
                        return "3";
                    default:
                        return "0";

                }
            }
        });

        mainTab = findViewById(R.id.main_tab);
        mainTab.setupWithViewPager(mainPager);
        mainTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0 ){
                    canScroll = true;
                }else {
                    canScroll = false;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        mainPager.setOffscreenPageLimit(4);
        mBottomBehavior = (BottomBehavior)((CoordinatorLayout.LayoutParams) mainTab.getLayoutParams()).getBehavior();
        mBottomBehavior.setOnCanScrollCallback(this);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void getUserAvatar() {
        preferences = getSharedPreferences(
                getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        Log.i(TAG, preferences.getString("domain", null));
        Log.i(TAG, preferences.getString("accessToken", null));
        mastodonApi.accountVerifyCredentials().enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                Log.i(TAG, response.isSuccessful()+"");
                Log.i(TAG, response.body().id+"");
                Log.i(TAG, response.body().username+"");
            }

            @Override
            public void onFailure(Call<Account> call, Throwable throwable) {
                Log.i(TAG, "fail"+throwable);
            }
        });
    }

    public void bringViewPagerToFront(){
        mainTab.setVisibility(View.GONE);
        mBottomBehavior.showBottom();
    }

    public void bringViewPagerToBack(){
        mainTab.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean callbackCanScroll() {
        return canScroll;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            if (!BackHandlerHelper.handleBackPress(this)) {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_my_favourite:
                Toast.makeText(MainActivity.this, "click favourite", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_my_draft:
                Toast.makeText(MainActivity.this, "click draft", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_my_filter:
                Toast.makeText(MainActivity.this, "click filter", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
                break;

            case R.id.nav_log_out:

                EditDialogFragment editDialogFragment = EditDialogFragment.newInstance("确认退出?");
                editDialogFragment.show(getSupportFragmentManager(), "logOutConfirmDialog");


                break;
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        preferences = getSharedPreferences(
                getString(R.string.preferences_file_key), Context.MODE_PRIVATE);
        boolean commitResult = preferences.edit().clear().commit();
        if (commitResult){
            Toast.makeText(MainActivity.this, R.string.success_account_log_out, Toast.LENGTH_SHORT).show();
            Intent logOutIntent = new Intent(MainActivity.this, SplashActivity.class);
            startActivity(logOutIntent);
            finish();
        }else {
            Toast.makeText(MainActivity.this, R.string.error_account_log_out, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Toast.makeText(MainActivity.this, R.string.cancel, Toast.LENGTH_SHORT).show();
    }
}
