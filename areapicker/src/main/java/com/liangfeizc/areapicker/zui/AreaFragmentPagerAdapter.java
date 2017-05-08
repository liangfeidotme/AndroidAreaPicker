package com.liangfeizc.areapicker.zui;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class AreaFragmentPagerAdapter extends FragmentPagerAdapter implements OnAreaPickedListener {
    private OnAreaPickedListener onAreaPickedListener;

    private List<AreaModel> initialAreaModels;

    private List<String> titles;
    private AreaModel[] selectedAreaModels;
    private AreaListFragment[] fragments;

    public AreaFragmentPagerAdapter(FragmentManager fm, List<String> titles) {
        super(fm);
        this.titles = titles;
        fragments = new AreaListFragment[titles.size()];
        selectedAreaModels = new AreaModel[titles.size()];
    }

    @Override
    public Fragment getItem(int position) {
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

    public AreaModel[] getSelectedAreaModels() {
        return selectedAreaModels;
    }

    @Override
    public void onPicked(int pagePosition, AreaModel pickedAreaModel) {
        final int nextPosition = pagePosition + 1;
        if (nextPosition < fragments.length) {
            fragments[nextPosition].setAreaModels(pickedAreaModel.subAreas);
        }

        if (!pickedAreaModel.equals(selectedAreaModels[pagePosition])) {
            selectedAreaModels[pagePosition] = pickedAreaModel;
            if (pickedAreaModel.subAreas != null) {
                for (AreaModel area : pickedAreaModel.subAreas) {
                    area.isSelected = false;
                }
            }
        }

        if (onAreaPickedListener != null) {
            onAreaPickedListener.onPicked(pagePosition, pickedAreaModel);
        }
    }
}
