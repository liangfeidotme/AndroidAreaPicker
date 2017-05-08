package com.liangfeizc.areapicker.zui;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.List;

public class AreaFragmentPagerAdapter extends FragmentPagerAdapter implements OnAreaPickedListener {
    private List<String> titles;
    private OnAreaPickedListener onAreaPickedListener;

    private AreaListFragment[] fragments;
    private List<AreaModel> initialAreaModels;

    public AreaFragmentPagerAdapter(FragmentManager fm, List<String> titles) {
        super(fm);
        this.titles = titles;
        fragments = new AreaListFragment[titles.size()];
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(ZanAreaPicker.TAG, "create pager " + position);
        AreaListFragment fragment = AreaListFragment.newInstance(position);
        fragment.setOnAreaPickedListener(this);
        fragments[position] = fragment;

        // setup area list in the first page.
        if (position == 0) {
            fragment.setAreaModels(initialAreaModels);
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return titles == null ? 0 : titles.size();
    }

    public void setInitialAreaModels(List<AreaModel> areaModels) {
        initialAreaModels = areaModels;
    }

    public void setOnAreaPickedListener(OnAreaPickedListener onAreaPickedListener) {
        this.onAreaPickedListener = onAreaPickedListener;
    }

    @Override
    public void onPicked(int pagePosition, AreaModel pickedAreaModel) {
        Log.d(ZanAreaPicker.TAG, "picked position " + pagePosition);
        fragments[pagePosition + 1].setAreaModels(pickedAreaModel.subAreas);

        if (onAreaPickedListener != null) {
            onAreaPickedListener.onPicked(pagePosition, pickedAreaModel);
        }
    }
}
