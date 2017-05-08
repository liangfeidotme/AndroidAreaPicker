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
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.liangfeizc.areapicker.R;

import java.util.ArrayList;
import java.util.List;

public class ZanAreaPicker extends DialogFragment implements View.OnClickListener, OnAreaPickedListener {
    public static final String TAG = "ZanAreaPicker";

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AreaFragmentPagerAdapter pagerAdapter;
    private TextView okButton;
    private AreaModel areaModel;

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

        List<String> titles = new ArrayList<>();
        titles.add(getString(R.string.province));
        titles.add(getString(R.string.city));
        titles.add(getString(R.string.area));
        pagerAdapter = new AreaFragmentPagerAdapter(getChildFragmentManager(), titles);
        pagerAdapter.setOnAreaPickedListener(this);
        pagerAdapter.setInitialAreaModels(areaModel.subAreas);

        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "view pager pos:" + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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

    }

    @Override
    public void onPicked(int pagePosition, AreaModel pickedAreaModel) {
        Log.d(TAG, "ZanAreaPicker#onPicked");
        viewPager.setCurrentItem(pagePosition + 1, true);
    }
}
