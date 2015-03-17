package com.liangfeizc.areapicker;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

/**
 * Created by liangfei on 3/12/15.
 */
public class AreaPicker extends LinearLayout implements NumberPicker.OnValueChangeListener {
    private static final String TAG = "AreaPicker";

    private static final int MAX_LEVEL = 3;

    private NumberPicker[] allNumberPickers;
    private OnAreaChangeListener areaChangeListener;

    public AreaPicker(Context context) {
        this(context, null);
    }

    public AreaPicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AreaPicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        setBackgroundColor(Color.WHITE);
        setOrientation(HORIZONTAL);

        allNumberPickers = new NumberPicker[MAX_LEVEL];
    }

    public void setData(String[] areas, int selectedIndex, int level) {
        if (level >= MAX_LEVEL) {
            return;
        }

        int childCount = getChildCount();
        for (int i = childCount - 1; i >= level; i--) {
            getChildAt(i).setVisibility(View.GONE);
        }

        NumberPicker picker = allNumberPickers[level];
        if (picker == null) {
            picker = createNumberPicker(areas, level);
            allNumberPickers[level] = picker;
            addView(picker);
        } else {
            picker.setVisibility(View.VISIBLE);
            picker.setDisplayedValues(null);
            picker.setMinValue(0);
            picker.setMaxValue(areas.length - 1);
            picker.setDisplayedValues(areas);
        }

        picker.setValue(selectedIndex);

        areaChangeListener.onAreaChange(level, selectedIndex);
    }

    public void setData(String[] areas, int selectedIndex) {
        setData(areas, selectedIndex, 0);
    }

    public void setData(String[] areas) {
        setData(areas, 0);
    }

    public NumberPicker createNumberPicker(final String[] names, final int level) {
        NumberPicker numberPicker = new NumberPicker(getContext());
        numberPicker.setTag(level);

        LinearLayout.LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = 5;
        params.rightMargin = 5;

        numberPicker.setLayoutParams(params);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setOnValueChangedListener(this);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(names.length - 1);
        numberPicker.setDisplayedValues(names);

        return numberPicker;
    }

    public void setOnAreaChangeListener(OnAreaChangeListener listener) {
        areaChangeListener = listener;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        int level = (int) picker.getTag();
        areaChangeListener.onAreaChange(level, newVal);
    }

    public interface OnAreaChangeListener {
        public void onAreaChange(int level, int index);
    }
}
