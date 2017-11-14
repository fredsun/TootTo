package org.tootto;

import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.tootto.ui.fragment.FragmentTransFirst;
import org.tootto.ui.fragment.view.NonSwipeableViewPager;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    TabLayout mainTab;
    NonSwipeableViewPager mainPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentList.add(FragmentTransFirst.newInstance());
        fragmentList.add(FragmentTransFirst.newInstance());
        fragmentList.add(FragmentTransFirst.newInstance());
        fragmentList.add(FragmentTransFirst.newInstance());


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
    }
}
