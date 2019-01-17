package com.liangfeizc.areapicker.zui;

import android.os.Bundle;
import androidx.fragment.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.List;


public class AreaListFragment extends ListFragment {
    private AreaListAdapter adapter;
    private OnAreaPickedListener onAreaPickedListener;
    private List<AreaModel> areaModels;
    private int pageIndex;

    public static AreaListFragment newInstance(int pageIndex) {
        AreaListFragment fragment = new AreaListFragment();
        fragment.pageIndex = pageIndex;
        return fragment;
    }

    public void setAreaModels(List<AreaModel> areaModels) {
        this.areaModels = areaModels;
        if (adapter != null) {
            adapter.clear();
            adapter.addAll(areaModels);
        }
    }

    public void setOnAreaPickedListener(OnAreaPickedListener listener) {
        onAreaPickedListener = listener;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new AreaListAdapter(getActivity());

        if (areaModels != null) {
            adapter.clear();
            adapter.addAll(areaModels);
        }
        setListAdapter(adapter);

        getListView().setDivider(null);
        getListView().setVerticalScrollBarEnabled(false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.d(ZanAreaPicker.TAG, "RFAreaListFragment#onListItemClick(" + pageIndex + ")");
        adapter.setSelected(position);
        if (onAreaPickedListener != null) {
            onAreaPickedListener.onAreaPicked(pageIndex, (AreaModel) l.getItemAtPosition(position));
        }
    }
}
