package org.tootto.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.XmlRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SlidingPaneLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.tootto.R;
import org.tootto.fragment.SettingDetailFragment;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by fred on 2018/1/4.
 */

public class SettingActivity extends BaseActivity implements PreferenceFragmentCompat.OnPreferenceStartFragmentCallback, AdapterView.OnItemClickListener, SettingDetailFragment.FragmentSPChangeListener {
    ListView listView;
    SlidingPaneLayout slidingPanel;
    EntriesAdapter mEntriesAdapter;
    static final String TAG = "SettingActivity";
    boolean mShouldRestart = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        listView = findViewById(R.id.category_list);
        slidingPanel = findViewById(R.id.sliding_panel);
        slidingPanel.setSliderFadeColor(0);
        mEntriesAdapter = new EntriesAdapter(this);
        initEntries();
        listView.setAdapter(mEntriesAdapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(this);
        if (savedInstanceState == null){
            Uri data = getIntent().getData();
            String initialTag = null;
            if (data!=null){
                initialTag = data.getAuthority();
            }
            int initialItem = -1;
            int firstEntry = -1;

            for (int i = 0; i< mEntriesAdapter.getCount(); i++){
                Object item = mEntriesAdapter.getItem(i);
                //打开时显示第一个PreferenceEntry
                if (item instanceof PreferenceEntry){
                    if (firstEntry == -1){
                        firstEntry = i;
                    }
                    //如果有在authority里设置tag,则指定tagPreferenceScreen
                    if (initialTag!= null && initialTag == ((PreferenceEntry) item).tag){
                        initialItem = i;
                        break;
                    }

                }
            }
            if (initialItem == -1){
                initialItem = firstEntry;
            }
            if (initialItem != -1){
                openDetails(initialItem);
                listView.setItemChecked(initialItem, true);

            }
        }
    }

    private void openDetails(int position) {
        if(!(mEntriesAdapter.getItem(position) instanceof PreferenceEntry)){
            return;
        }
        PreferenceEntry entry = (PreferenceEntry) mEntriesAdapter.getItem(position);
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        supportFragmentManager.popBackStackImmediate(null, 0);
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        if (entry.preference != 0){
            Bundle args = new Bundle();
            args.putInt("resid", entry.preference);
            Fragment instantiate = Fragment.instantiate(this, SettingDetailFragment.class.getName(),args);
            fragmentTransaction.replace(R.id.detail_fragment_container, instantiate);
        }else if (entry.fragment != null){
            fragmentTransaction.replace(R.id.detail_fragment_container, Fragment.instantiate(this, entry.fragment, entry.args));
        }
        fragmentTransaction.setBreadCrumbTitle(entry.title);
        fragmentTransaction.commit();
    }

    private void initEntries() {
        mEntriesAdapter.addHeader("功能");
        mEntriesAdapter.addPreference("network", R.drawable.ic_wifi_black_24dp, "网络", R.xml.pref_network);
        mEntriesAdapter.addPreference("theme", R.drawable.ic_color_lens_black_24dp, "主题", R.xml.pref_theme);
        mEntriesAdapter.addHeader("关于");
        mEntriesAdapter.addPreference("me", R.drawable.ic_info_black_24dp, "关于", R.xml.pref_me);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        openDetails(position);
        view.setSelected(true);
    }

    /**
     * stolen from super.onBackPressed()
     * 如果有需要, 在 pop 出细化的 Fragment 后再按 back 弹出 DialogFragment
     */
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        final boolean isStateSaved = fragmentManager.isStateSaved();
        if (isStateSaved && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
            // Older versions will throw an exception from the framework
            // FragmentManager.popBackStackImmediate(), so we'll just
            // return here. The Activity is likely already on its way out
            // since the fragmentManager has already been saved.
            return;
        }
        if (isStateSaved || !fragmentManager.popBackStackImmediate()) {
            if (mShouldRestart){
                DialogFragment dialogFragment = RestartConfirmFragment.newInstance(R.string.restart_confirm_title);
                dialogFragment.show(getSupportFragmentManager(), "restartConfirmDialog");
                SharedPreferences preference = getSharedPreferences("preference", Context.MODE_PRIVATE);
//            Log.i(TAG, "test_key1"+preference.getString("test_key", ""));
//            Log.i(TAG, "save_data1"+preference.getBoolean("save_data", false));
            }else {
                super.onBackPressed();
            }


        }
    }

    /**
     * stolen from https://github.com/TwidereProject/Twidere-Android/blob/master/twidere/src/main/kotlin/org/mariotaku/twidere/activity/SettingsActivity.kt
     * preference 二次跳转到更细化的子 Fragment
     */
    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, android.support.v7.preference.Preference pref) {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        Fragment instantiate = Fragment.instantiate(this, pref.getFragment(), pref.getExtras());
        fragmentTransaction.replace(R.id.detail_fragment_container, instantiate);
        fragmentTransaction.addToBackStack(pref.getTitle().toString());
        fragmentTransaction.commit();
        return true;
    }

    @Override
    public void onShouldRestart(boolean should) {
        mShouldRestart = should;
    }


    interface Entry{
        void bind(View view);
    }

    /**
     * 左侧 listView 的条目
     */
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

    /**
     * 左侧 listView 的标题头
     */
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


    /**
     * back后特殊情况需要确认的 Dialog
     */
    public static class RestartConfirmFragment extends DialogFragment{
        public static RestartConfirmFragment newInstance(int title){
            RestartConfirmFragment restartConfirmFragment = new RestartConfirmFragment();
            Bundle args = new Bundle();
            args.putInt("title", title);
            restartConfirmFragment.setArguments(args);
            return restartConfirmFragment;
        }
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int title = getArguments().getInt("title");
            return new AlertDialog.Builder(getActivity())
                    .setTitle(title)
                    .setPositiveButton("保存",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivity().finish();
                                }
                            })
                    .setNegativeButton("不保存",
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivity().finish();
                                }
                            }
                    )
                    .create();
        }
    }
}


