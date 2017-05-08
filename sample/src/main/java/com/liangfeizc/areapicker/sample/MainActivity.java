package com.liangfeizc.areapicker.sample;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.liangfeizc.areapicker.tb.AreaPicker;
import com.liangfeizc.areapicker.zui.AreaModel;
import com.liangfeizc.areapicker.zui.FileUtils;
import com.liangfeizc.areapicker.zui.ZanAreaPicker;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements AreaPicker.OnAreaChangeListener {
    private AreaPicker areaPicker;
    private PopupWindow areaPickerWindow;

    private List<Area> mainAreas;
    private AreaNode rootAreaNode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainAreas = Utils.getMainAreas(this);
        rootAreaNode = new AreaNode();
        rootAreaNode.next = new AreaNode(mainAreas, null);
    }

    public void areaClick(View view) {
        if (areaPickerWindow == null) {
            areaPicker = createAreaPicker();
            areaPicker.setOnAreaChangeListener(this);
            areaPickerWindow = new PopupWindow(areaPicker,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            areaPickerWindow.setOutsideTouchable(true);
            areaPickerWindow.setBackgroundDrawable(new ColorDrawable(0xff));
            areaPickerWindow.setAnimationStyle(android.R.style.Animation_Dialog);
            areaPickerWindow.setFocusable(true);
            areaPickerWindow.setTouchable(true);
        }
        areaPicker.setData(Utils.extractNames(mainAreas));
        areaPickerWindow.showAsDropDown(view);
    }

    public AreaPicker createAreaPicker() {
        AreaPicker picker = new AreaPicker(this);
        picker.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return picker;
    }

    @Override
    public void onAreaChange(int level, int index) {
        Log.d("AreaPicker", "level:" + level + ",index:" + index);
        AreaNode root = rootAreaNode;
        for (int i = 0; i <= level; i++) {
           root = root.next;
        }

        String id = root.areas.get(index).id;
        List<Area> subAreas = Utils.getSubAreasById(this, id);
        if (subAreas != null) {
            root.next = new AreaNode(subAreas, null);
            areaPicker.setData(Utils.extractNames(subAreas), 0, level + 1);
        }
    }

    public void zuiAreaClick(View view) {
        String jsonStr = FileUtils.readAssetFileToString(this, "areas.json");
        AreaModel areaModel = new Gson().fromJson(jsonStr, AreaModel.class);
        ZanAreaPicker.create(areaModel).show(getSupportFragmentManager(), ""); // why empty string
    }

    public class AreaNode {
        public List<Area> areas;
        public AreaNode next;

        public AreaNode() {
            this(null, null);
        }

        public AreaNode(List<Area> areas, AreaNode next) {
            this.areas = areas;
            this.next = next;
        }
    }
}
