package org.tootto.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;

import org.tootto.util.Utils;

/**
 * gradle需加入v14包的support
 * Created by fred on 2018/1/5.
 */

public class SettingDetailFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{
    final String TAG = "SettingDetailFragment";

    /**
     * 创建布局(通过resId)
     *
     * @param savedInstanceState
     * @param rootKey
     * 参考自 https://developer.android.com/reference/android/support/v7/preference/PreferenceFragmentCompat.html#setPreferencesFromResource(int, java.lang.String)
     */
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        //设置的sp存为名为"preference"的sp;
//        getPreferenceManager().setSharedPreferencesName("preference");
//        getPreferenceManager().
        PreferenceScreen defaultPreferenceScreen = getPreferenceScreen();
        PreferenceScreen preferenceScreen;
        if (defaultPreferenceScreen != null){
            defaultPreferenceScreen.removeAll();
            preferenceScreen = defaultPreferenceScreen;
        }else {
            preferenceScreen = getPreferenceManager().createPreferenceScreen(getActivity());
        }
        setPreferenceScreen(preferenceScreen);
        Bundle arguments = getArguments();
        Object rawResId = arguments.get("resid");
        int resId;
        if (rawResId instanceof Integer){
                resId = ((Number)rawResId).intValue();
        }else if (rawResId instanceof String){
            resId = Utils.getResId(getActivity(), (String) rawResId);
        }else {
            resId = 0;
        }
        if (resId != 0){
            addPreferencesFromResource(resId);
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference == null){
            return;
        }
        Bundle extras = preference.getExtras();
        if (extras!= null){
            if (extras.containsKey("extra_should_restart")){
                ((FragmentSPChangeListener)getActivity()).onShouldRestart(true);
            }
            Log.i(TAG, "not contain");
        }
        Log.i(TAG, "change"+findPreference(key).getExtras());
    }

    public interface FragmentSPChangeListener{
        void onShouldRestart(boolean should);
    }

}
