package com.liangfeizc.areapicker.zui;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.liangfeizc.areapicker.R;

import java.util.ArrayList;
import java.util.List;

public class ZanAreaPicker extends DialogFragment implements View.OnClickListener,
        OnAreaPickedListener {
    public static final String TAG = "ZanAreaPicker";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AreaFragmentPagerAdapter pagerAdapter;
    private TextView okButton;
    private AreaModel areaModel;

    private List<String> tabTitles;

    public static ZanAreaPicker create(AreaModel areaModel) {
        ZanAreaPicker picker = new ZanAreaPicker();
        picker.areaModel = areaModel;
        return picker;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.BottomDialog);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choose_area);
        dialog.setCanceledOnTouchOutside(true);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.gravity = Gravity.BOTTOM;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = getDisplayHeight() * 2 / 3;
        window.setAttributes(lp);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // views
        View view = inflater.inflate(R.layout.dialog_choose_area, null, false);
        tabLayout = (TabLayout) view.findViewById(R.id.area_tablayout);
        viewPager = (ViewPager) view.findViewById(R.id.area_viewpager);

        // interactions
        tabLayout.setupWithViewPager(viewPager);

        tabTitles = new ArrayList<>();
        tabTitles.add(getString(R.string.province));
        tabTitles.add(getString(R.string.city));
        tabTitles.add(getString(R.string.area));

        pagerAdapter = new AreaFragmentPagerAdapter(getChildFragmentManager(), tabTitles);
        pagerAdapter.setOnAreaPickedListener(this);
        pagerAdapter.setInitialAreaModels(areaModel.subAreas);

        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(0);

        okButton = (TextView) view.findViewById(R.id.button_area_choose_ok);
        okButton.setOnClickListener(this);
        okButton.setEnabled(false);

        view.findViewById(R.id.separator).setVisibility(
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? View.GONE : View.VISIBLE);

        /* 动画 */
        getDialog().getWindow().setWindowAnimations(R.style.areaAnim);

        /* activity背部阴影 */
        dimBackground(getActivity(), 1.0f, 0.5f);
        return view;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        dimBackground(getActivity(), 0.5f, 1.0f);
        super.onDismiss(dialog);
    }

    private int getDisplayHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    private void dimBackground(Activity activity, final float from, final float to) {
        final Window window = activity.getWindow();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(from, to);
        valueAnimator.setDuration(500);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.alpha = (Float) animation.getAnimatedValue();
                window.setAttributes(params);
            }
        });

        valueAnimator.start();
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }

    public void refreshTabHeaderStatus(int pagePosition, AreaModel pickedAreaModel) {
        ensureTabCustomView(tabLayout.getTabAt(pagePosition)).setText(pickedAreaModel.name);

        if (pagerAdapter.isAreaChanged()) {
            AreaModel[] selectedAreas = pagerAdapter.getSelectedAreaModels();
            for (int i = pagePosition + 1; i < selectedAreas.length; i++) {
                ensureTabCustomView(tabLayout.getTabAt(i)).setText(tabTitles.get(i));
            }
        }

        // disable the following tabs
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        AreaModel[] selectedAreaModels = pagerAdapter.getSelectedAreaModels();
        for (int i = 0; i < selectedAreaModels.length; i++) {
            final AreaModel area = selectedAreaModels[i];
            tabStrip.getChildAt(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return area == null;
                }
            });

            ensureTabCustomView(tabLayout.getTabAt(i)).setTextColor(area != null ?
                    ContextCompat.getColor(getContext(), R.color.tab_selected_text_color) :
                    ContextCompat.getColor(getContext(), R.color.tab_unselected_text_color));
        }
    }

    private TextView ensureTabCustomView(TabLayout.Tab tab) {
        if (tab.getCustomView() == null) {
            tab.setCustomView(R.layout.area_picker_custom_tab);
        }
        return (TextView) tab.getCustomView();
    }

    @Override
    public void onPicked(int pagePosition, AreaModel pickedAreaModel) {
        refreshTabHeaderStatus(pagePosition, pickedAreaModel);

        int currentPosition = pagePosition + 1;
        boolean hasMore = currentPosition < pagerAdapter.getCount();
        okButton.setEnabled(!hasMore);
        if (hasMore) {
            viewPager.setCurrentItem(currentPosition, true);
        }
    }
}
