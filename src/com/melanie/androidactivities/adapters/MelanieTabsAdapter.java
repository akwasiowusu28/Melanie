package com.melanie.androidactivities.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import com.melanie.androidactivities.support.Utils;

public class MelanieTabsAdapter extends FragmentPagerAdapter {

    private SparseArray<Class<? extends Fragment>> fragments;
    private boolean isDaily;

    public MelanieTabsAdapter(FragmentManager fm,
                                  SparseArray<Class<? extends Fragment>> fragments, boolean isDaily) {
        super(fm);
        this.fragments = fragments;
        this.isDaily = isDaily;
    }

    @Override
    public Fragment getItem(int index) {

        Fragment fragment = null;
        Class<? extends Fragment> klass = fragments.get(index);
        try {
            fragment = klass.newInstance();
            Bundle bundle = new Bundle();
            bundle.putBoolean(Utils.Constants.IS_DAILY,isDaily);
            fragment.setArguments(bundle);
        } catch (IllegalAccessException | InstantiationException e) {
            // TODO: log it
        }

        return fragment;
    }

    @Override
    public int getCount() {

        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return  position == 0 ? "Table" : "Chart";
    }

}
