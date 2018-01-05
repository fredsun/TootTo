package org.tootto.activity;

import android.content.Context;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.XmlRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.tootto.R;

import java.util.ArrayList;

/**
 * Created by fred on 2018/1/4.
 */

public class SetActivity extends AppCompatActivity implements PreferenceFragment.OnPreferenceStartFragmentCallback{
    ListView listview;
    EntriesAdapter mEntriesAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        listview = findViewById(R.id.category_list);
        mEntriesAdapter = new EntriesAdapter(this);
        initEntries();
        listview.setAdapter(mEntriesAdapter);
    }

    private void initEntries() {
        mEntriesAdapter.addHeader("功能");
        mEntriesAdapter.addPreference("网络", R.drawable.ic_wifi_black_24dp, "网络1", R.xml.pref_network);
        mEntriesAdapter.addHeader("关于");

    }


    //stolen from https://github.com/TwidereProject/Twidere-Android/blob/master/twidere/src/main/kotlin/org/mariotaku/twidere/activity/SettingsActivity.kt
    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment caller, Preference preference) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        Fragment instantiate = Fragment.instantiate(this, preference.getFragment(), preference.getExtras());
        fragmentTransaction.replace(R.id.detail_fragment_container, instantiate);
        fragmentTransaction.addToBackStack(preference.getTitle().toString());
        fragmentTransaction.commit();
        return true;
    }

    private class EntriesAdapter extends BaseAdapter{
        final int VIEW_TYPE_PREFERENCE_ENTRY = 0;
        final int VIEW_TYPE_HEADER_ENTRY = 1;
        Context context;
        LayoutInflater layoutInflater;
        ArrayList<Entry> entries = new ArrayList<>();

        public EntriesAdapter(Context context) {
            this.context = context;
            layoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return entries.size();
        }

        @Override
        public Object getItem(int position) {
            return entries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (long)getItem(position).hashCode();
        }



        @Override
        public boolean isEnabled(int position) {
            return getItemViewType(position) == VIEW_TYPE_PREFERENCE_ENTRY;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            Entry item = (Entry) getItem(position);
            if (item instanceof PreferenceEntry){
                return VIEW_TYPE_PREFERENCE_ENTRY;
            }else if (item instanceof HeadEntry){
                return VIEW_TYPE_HEADER_ENTRY;
            }
            throw new UnsupportedOperationException();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            int itemViewType = getItemViewType(position);
            Entry entry = (Entry) getItem(position);
            View itemView;
            if (convertView != null){
                itemView = convertView;
            }else {
                switch (itemViewType){
                    case VIEW_TYPE_PREFERENCE_ENTRY:
                        itemView =  layoutInflater.inflate(R.layout.item_preference_header_item, parent, false);
                        break;
                    case VIEW_TYPE_HEADER_ENTRY:
                        itemView =  layoutInflater.inflate(R.layout.item_preference_header_category, parent, false);
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
            }
            entry.bind(itemView);
            return itemView;
        }

        void addHeader(String title){
            entries.add(new HeadEntry(title));
            notifyDataSetChanged();
        }

        void addPreference(String tag, @DrawableRes int icon, String title, @XmlRes int preference){
            entries.add(new PreferenceEntry(tag, icon, title, preference, null, null));
            notifyDataSetChanged();
        }

        void addPreference(String tag, @DrawableRes int icon, String title, Class fragment, Bundle args){
            entries.add(new PreferenceEntry(tag, icon, title, 0, fragment.getName(), args));
            notifyDataSetChanged();
        }
    }

    interface Entry{
        void bind(View view);
    }

    class PreferenceEntry implements Entry {
        String tag;
        int icon;
        String title;
        int preference;
        String fragment;
        Bundle args;

        public PreferenceEntry(String tag, int icon, String title, int preference, String fragment, Bundle args) {
            this.tag = tag;
            this.icon = icon;
            this.title = title;
            this.preference = preference;
            this.fragment = fragment;
            this.args = args;
        }

        @Override
        public void bind(View view) {
            ImageView imageView;
            imageView = ((ImageView)view.findViewById(android.R.id.icon));
            imageView.setImageResource(icon);
            ((TextView)view.findViewById(android.R.id.title)).setText(title);
        }
    }

    class HeadEntry implements Entry{
        String title;

        public HeadEntry(String title) {
            this.title = title;
        }

        @Override
        public void bind(View view) {
            ((TextView)view.findViewById(android.R.id.title)).setText(title);
        }
    }
}
