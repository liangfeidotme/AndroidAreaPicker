package me.liangfei.areapicker.deprecated;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import me.liangfei.areapicker.R;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ZanAreaPicker extends DialogFragment implements View.OnClickListener,
        OnAreaPickedListener {
    public static final String TAG = "ZanAreaPicker";

    public interface OnPickAreaListener {
        void onPick(List<AreaModel> areas);
    }

    public static final int DEFAULT_TAB_COUNT = 3;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AreaFragmentPagerAdapter pagerAdapter;
    private TextView okBtn;
    private AreaModel areaModel;

    private List<String> tabTitles;
    private List<String> addressParts;

    private OnPickAreaListener onPickAreaListener;

    public static ZanAreaPicker create(AreaModel areaModel) {
        ZanAreaPicker picker = new ZanAreaPicker();
        picker.areaModel = areaModel;
        return picker;
    }

    public void setAddressParts(List<String> addressParts) {
        this.addressParts = addressParts;
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
        tabLayout = view.findViewById(R.id.area_tablayout);
        viewPager = view.findViewById(R.id.area_viewpager);

        okBtn = view.findViewById(R.id.button_area_choose_ok);
        okBtn.setOnClickListener(this);
        okBtn.setEnabled(false);

        tabTitles = new ArrayList<>(DEFAULT_TAB_COUNT);
        tabTitles.add(getString(R.string.province));
        tabTitles.add(getString(R.string.city));
        tabTitles.add(getString(R.string.area));

        // add three tabs for province/city/district
        for (int pos = 0; pos < DEFAULT_TAB_COUNT; pos++) {
            tabLayout.addTab(tabLayout.newTab().setTag(pos));
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem((int) tab.getTag(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pagerAdapter = new AreaFragmentPagerAdapter(getChildFragmentManager(), tabTitles);
        pagerAdapter.setOnAreaPickedListener(this);

        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setCurrentItem(0);

        pagerAdapter.setAreaChanged(true);
        refreshTabHeaderStatus(-1, null);

        pagerAdapter.setInitialAreaModels(areaModel.subAreas, addressParts);

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
        if (onPickAreaListener != null) {
            onPickAreaListener.onPick(Arrays.asList(pagerAdapter.getSelectedAreaModels()));
        }
        dismiss();
    }

    public void refreshTabHeaderStatus(int pagePosition, AreaModel pickedAreaModel) {
        Log.d(TAG, "refreshTabHeaderStatus(");

        if (pickedAreaModel != null && pagePosition >= 0) {
            Log.d(TAG, "refreshTabHeaderStatus(" + pagePosition + ", " + pickedAreaModel.name + ")");
            ensureTabCustomView(tabLayout.getTabAt(pagePosition)).setText(pickedAreaModel.name);
        }

        if (pagerAdapter.isAreaChanged()) {
            AreaModel[] selectedAreas = pagerAdapter.getSelectedAreaModels();
            for (int i = pagePosition + 1; i < selectedAreas.length; i++) {
                Log.d(TAG, "isAreaChanged from " + (pagePosition + 1));
                ensureTabCustomView(tabLayout.getTabAt(i)).setText(tabTitles.get(i));
            }
        }

        // disable the following tabs
        LinearLayout tabStrip = ((LinearLayout) tabLayout.getChildAt(0));
        AreaModel[] selectedAreaModels = pagerAdapter.getSelectedAreaModels();

        int indexOfFirstNull = 0;
        for (; indexOfFirstNull < selectedAreaModels.length; indexOfFirstNull++) {
            enableTabAt(tabStrip, indexOfFirstNull, true);
            if (selectedAreaModels[indexOfFirstNull] == null) {
                break;
            }
        }

        for (int i = indexOfFirstNull + 1; i < selectedAreaModels.length; i++) {
            enableTabAt(tabStrip, i, false);
        }
    }

    private void enableTabAt(LinearLayout tabStrip, int position, boolean enabled) {
        tabStrip.getChildAt(position).setEnabled(enabled);
        ensureTabCustomView(tabLayout.getTabAt(position)).setTextColor(enabled ?
                ContextCompat.getColor(getContext(), R.color.tab_selected_text_color) :
                ContextCompat.getColor(getContext(), R.color.tab_unselected_text_color));
    }

    private TextView ensureTabCustomView(TabLayout.Tab tab) {
        if (tab.getCustomView() == null) {
            tab.setCustomView(R.layout.area_picker_custom_tab);
        }
        return (TextView) tab.getCustomView();
    }

    @Override
    public void onAreaPicked(int pagePosition, AreaModel pickedAreaModel) {
        refreshTabHeaderStatus(pagePosition, pickedAreaModel);

        int currentPosition = pagePosition + 1;
        boolean hasMore = currentPosition < pagerAdapter.getCount();
        okBtn.setEnabled(!hasMore);
        okBtn.setTextColor(okBtn.isEnabled() ?
                ContextCompat.getColor(getContext(), R.color.tab_ok_button_enabled_color) :
                ContextCompat.getColor(getContext(), R.color.tab_unselected_text_color));
        if (hasMore) {
            viewPager.setCurrentItem(currentPosition, true);
        }
    }

    public void setOnPickAreaListener(OnPickAreaListener listener) {
        onPickAreaListener = listener;
    }
}
