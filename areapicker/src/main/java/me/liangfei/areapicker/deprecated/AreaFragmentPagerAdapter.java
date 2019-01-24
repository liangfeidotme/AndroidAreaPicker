package me.liangfei.areapicker.deprecated;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import android.text.TextUtils;

import java.util.List;

public class AreaFragmentPagerAdapter extends FragmentPagerAdapter implements OnAreaPickedListener {
    private OnAreaPickedListener onAreaPickedListener;

    private List<AreaModel> initialAreaModels;

    private List<String> titles;
    private AreaModel[] selectedAreaModels;
    private AreaListFragment[] fragments;
    private boolean isAreaChanged;

    public AreaFragmentPagerAdapter(FragmentManager fm, List<String> titles) {
        super(fm);
        this.titles = titles;

        final int count = titles.size();
        selectedAreaModels = new AreaModel[titles.size()];

        fragments = new AreaListFragment[titles.size()];

        for (int i = 0; i < count; i++) {
            fragments[i] = AreaListFragment.newInstance(i);
            fragments[i].setOnAreaPickedListener(this);
        }
    }

    @Override
    public Fragment getItem(int position) {
        // setup area list in the first page.
        if (position == 0) {
            fragments[0].setAreaModels(initialAreaModels);
        }
        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public int getCount() {
        int showingCount = 0;
        for (int i = 0; i < selectedAreaModels.length; i++) {
            showingCount = i + 1;
            if (selectedAreaModels[i] == null) {
                break;
            }
        }

        return showingCount;
    }

    public void setInitialAreaModels(List<AreaModel> areaModels, List<String> addressParts) {
        initialAreaModels = areaModels;
        List<AreaModel> areas = areaModels;
        for (int i = 0, size = addressParts.size(); i < size; i++) {
            for (AreaModel area : areas) {
                if (TextUtils.equals(addressParts.get(i), area.name)) {
                    area.isSelected = true;
                    selectedAreaModels[i] = area;
                    fragments[i].setAreaModels(areas);
                    areas = area.subAreas;
                    onAreaPicked(i, area);
                    break;
                }
            }
        }
    }

    public void setOnAreaPickedListener(OnAreaPickedListener onAreaPickedListener) {
        this.onAreaPickedListener = onAreaPickedListener;
    }

    public AreaModel[] getSelectedAreaModels() {
        return selectedAreaModels;
    }

    @Override
    public void onAreaPicked(int pagePosition, AreaModel pickedAreaModel) {
        final int nextPosition = pagePosition + 1;
        if (nextPosition < fragments.length) {
            fragments[nextPosition].setAreaModels(pickedAreaModel.subAreas);
        }

        isAreaChanged = !pickedAreaModel.equals(selectedAreaModels[pagePosition]);
        // area changed
        if (isAreaChanged) {
            selectedAreaModels[pagePosition] = pickedAreaModel;
            if (pickedAreaModel.subAreas != null) {
                for (AreaModel area : pickedAreaModel.subAreas) {
                    area.isSelected = false;
                }
            }

            for (int i = pagePosition + 1, size = getCount(); i < size; i++) {
                selectedAreaModels[i] = null;
            }
        }

        notifyDataSetChanged();

        if (onAreaPickedListener != null) {
            onAreaPickedListener.onAreaPicked(pagePosition, pickedAreaModel);
        }
    }

    public void setAreaChanged(boolean changed) {
        isAreaChanged = changed;
    }

    public boolean isAreaChanged() {
        return isAreaChanged;
    }
}
