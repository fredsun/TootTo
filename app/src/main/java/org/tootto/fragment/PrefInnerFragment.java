package org.tootto.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceFragmentCompat;

import org.tootto.R;
import org.tootto.backinterface.FragmentBackHandler;

/**
 * Created by fred on 2018/1/11.
 */

public class PrefInnerFragment extends PreferenceFragmentCompat implements FragmentBackHandler {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_me);
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
