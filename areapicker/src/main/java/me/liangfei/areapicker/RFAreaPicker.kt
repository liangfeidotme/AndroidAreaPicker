package me.liangfei.areapicker

import android.animation.ValueAnimator
import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.*
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.liangfeizc.areapicker.R

/**
 * Created by LIANG.FEI on 16/1/2019.
 */

/**
 * Data class for an area unit.
 */
data class RFAreaModel(val id: Long,
                       val name: String,
                       val subList: List<RFAreaModel>?,
                       var isSelected: Boolean)

/**
 * Picker.
 */
class RFAreaPicker : DialogFragment() {
    companion object {
        @JvmStatic
        fun create(areaModel: RFAreaModel) =
                RFAreaPicker().apply {
                    this.areaModel = areaModel
                }
    }

    /**
     * Title of each tab.
     */
    private var tabTitles = mutableListOf<String>()

    /**
     * Area parts for initialization.
     */
    var addressParts: List<String> = emptyList()

    /**
     * Area model to store areas.
     */
    var areaModel: RFAreaModel? = null


    /**
     * UI elements.
     */
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager
    private lateinit var okBtn: TextView

    /**
     * Fragment pager adapter.
     */
    private lateinit var pagerAdapter: AreaFragmentPagerAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?) =
            Dialog(activity!!, R.style.BottomDialog).apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.dialog_choose_area)
                setCanceledOnTouchOutside(true)
                window?.apply {
                    attributes = attributes.apply {
                        gravity = Gravity.BOTTOM
                        width = WindowManager.LayoutParams.MATCH_PARENT
                        height = displayHeight * 2 / 3
                    }
                }
            }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.dialog_choose_area, null, false)

        // Initialize UI elements.
        tabLayout = view.findViewById(R.id.area_tablayout)
        viewPager = view.findViewById(R.id.area_viewpager)
        okBtn = view.findViewById<TextView>(R.id.button_area_choose_ok).apply {
            setOnClickListener {
                areaPickListener(pagerAdapter.selectedAreaModels.filterNotNull())
                dismiss()
            }
            isEnabled = false
        }

        // Initialize tab titles.
        tabTitles.apply {
            add(getString(R.string.province))
            add(getString(R.string.city))
            add(getString(R.string.area))

            forEachIndexed { index, _ ->
                tabLayout.addTab(tabLayout.newTab().setTag(index))
            }
        }

        // Tab listener to switch pagers.
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    viewPager.setCurrentItem(tab.tag as Int, true)
                    Log.d(TAG, "onTabSelected with ${tab.tag}")
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })

        // Initialize pager adapter.
        pagerAdapter = AreaFragmentPagerAdapter(childFragmentManager, tabTitles).apply {
            areaPickedListenerInner = this@RFAreaPicker.areaPickedListenerInner
        }

        // Initialize view pager.
        viewPager.apply {
            adapter = pagerAdapter
            addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
            currentItem = 0
        }

        // Notify area change.
        pagerAdapter.isAreaChanged = true

        refreshTabHeaderStatus(-1, null)

        areaModel?.subList?.let {
            pagerAdapter.initAreaModels(it, addressParts)
        }

        // UI effects (not important).
        dialog?.window?.setWindowAnimations(R.style.areaAnim)
        dimBackground(activity!!, 1.0f, 0.5f)

        return view
    }

    var areaPickListener: (List<RFAreaModel>) -> Unit = {}

    private val areaPickedListenerInner: AreaPickListenerInner = { pagePosition: Int, pickedRFAreaModel: RFAreaModel ->
        refreshTabHeaderStatus(pagePosition, pickedRFAreaModel)

        val currentPosition = pagePosition + 1
        val hasMore = currentPosition < pagerAdapter.count
        okBtn.apply {
            isEnabled = !hasMore
            setTextColor(ContextCompat.getColor(context!!,
                    if (isEnabled) R.color.tab_ok_button_enabled_color
                    else R.color.tab_unselected_text_color))
        }

        if (hasMore) {
            viewPager.setCurrentItem(currentPosition, true)
        }
    }

    private fun refreshTabHeaderStatus(pagePosition: Int, pickedRFAreaModel: RFAreaModel?) {
        if (pickedRFAreaModel != null && pagePosition >= 0) {
            ensureTabCustomView(tabLayout.getTabAt(pagePosition)!!).text = pickedRFAreaModel.name
        }

        if (pagerAdapter.isAreaChanged) {
            for (i in pagePosition + 1 until pagerAdapter.selectedAreaModels.size) {
                ensureTabCustomView(tabLayout.getTabAt(i)!!).text = tabTitles[i]
            }
        }

        val tabStrip = tabLayout.getChildAt(0) as LinearLayout
        pagerAdapter.selectedAreaModels.forEachIndexed { index, areaModel ->
            enableTabAt(tabStrip, index, areaModel != null)
        }
    }

    private fun enableTabAt(tabStrip: LinearLayout, position: Int, enabled: Boolean) {
        tabStrip.getChildAt(position).isEnabled = enabled
        ensureTabCustomView(tabLayout.getTabAt(position)!!).setTextColor(
                ContextCompat.getColor(context!!,
                        if (enabled) R.color.tab_selected_text_color
                        else R.color.tab_unselected_text_color))
    }

    private fun ensureTabCustomView(tab: TabLayout.Tab): TextView {
        if (tab.customView == null) {
            tab.setCustomView(R.layout.area_picker_custom_tab)
        }
        return tab.customView as TextView
    }

    private fun dimBackground(activity: Activity, from: Float, to: Float) {
        val window = activity.window
        val valueAnimator = ValueAnimator.ofFloat(from, to)
        valueAnimator.duration = 500
        valueAnimator.addUpdateListener { animation ->
            val params = window.attributes
            params.alpha = animation.animatedValue as Float
            window.attributes = params
        }

        valueAnimator.start()
    }

    override fun onDismiss(dialog: DialogInterface) {
        dimBackground(activity!!, 0.5f, 1.0f)
        super.onDismiss(dialog)
    }

    private val displayHeight: Int
        get() {
            val dm = DisplayMetrics()
            activity?.windowManager?.defaultDisplay?.getMetrics(dm)
            return dm.heightPixels
        }
}

/**
 * The adapter of the fragment pager.
 */
class AreaFragmentPagerAdapter(fm: FragmentManager, private var titles: List<String>)
    : FragmentPagerAdapter(fm) {

    private var initialAreaModels: List<RFAreaModel>? = null

    var selectedAreaModels: Array<RFAreaModel?> = arrayOfNulls(titles.size)
    var areaPickedListenerInner: AreaPickListenerInner? = null
    var isAreaChanged = false

    private val areaPickListenerInterceptor: AreaPickListenerInner = { pagePosition, pickedAreaModel ->
        val nextPosition = pagePosition + 1
        if (nextPosition < fragments.size) {
            fragments[nextPosition].areaModels = pickedAreaModel.subList
        }
        isAreaChanged = pickedAreaModel != selectedAreaModels[pagePosition]

        if (isAreaChanged) {
            selectedAreaModels[pagePosition] = pickedAreaModel
            pickedAreaModel.subList?.forEach { it.isSelected = false }

            for (i in pagePosition + 1 until count) {
                selectedAreaModels[i] = null
            }
        }

        notifyDataSetChanged()

        areaPickedListenerInner?.invoke(pagePosition, pickedAreaModel)
    }

    private var fragments: Array<RFAreaListFragment> = Array(titles.size) {
        RFAreaListFragment.newInstance(it).apply {
            pickListenerInner = areaPickListenerInterceptor
        }
    }

    /**
     * 初始化 area models
     */
    fun initAreaModels(areaModels: List<RFAreaModel>, addressParts: List<String>) {
        initialAreaModels = areaModels

        var areas: List<RFAreaModel>? = areaModels
        for (i in 0 until addressParts.size) {
            areas?.firstOrNull {
                it.name == addressParts[i]
            }?.apply {
                isSelected = true
            }?.let {
                selectedAreaModels[i] = it
                fragments[i].areaModels = areas
                areaPickListenerInterceptor(i, it)
                areas = it.subList
            }
        }
    }

    override fun getPageTitle(position: Int) = titles[position]

    override fun getCount(): Int {
        val indexOfFirstNull = selectedAreaModels.indexOfFirst { it == null }
        return if (indexOfFirstNull == -1) selectedAreaModels.size else indexOfFirstNull + 1
    }

    override fun getItem(position: Int): Fragment {
        if (position == 0) fragments[0].areaModels = initialAreaModels
        return fragments[position]
    }
}
