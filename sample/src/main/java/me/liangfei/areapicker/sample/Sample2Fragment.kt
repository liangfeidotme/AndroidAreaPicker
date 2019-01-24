package me.liangfei.areapicker.sample

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.PopupWindow
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import me.liangfei.areapicker.TBAreaPicker
import me.liangfei.areapicker.sample.databinding.FragmentSample2Binding
import me.liangfei.areapicker.sample.pojo.Area
import me.liangfei.areapicker.sample.utils.Utils

/**
 * Created by LIANG.FEI on 22/1/2019.
 */
class Sample2Fragment : Fragment(), TBAreaPicker.OnAreaChangeListener {

    private val selectedArea by lazy { ObservableField<String>() }

    private val areaPickerWindow by lazy {
        PopupWindow(areaPicker, WRAP_CONTENT, WRAP_CONTENT).apply {
            isOutsideTouchable = true
            setBackgroundDrawable(ColorDrawable(0xff))
            animationStyle = android.R.style.Animation_Dialog
            isFocusable = true
            isTouchable = true
        }
    }

    private val areaPicker by lazy {
        TBAreaPicker(requireContext()).apply {
            layoutParams = ViewGroup.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        }
    }
    private lateinit var rootAreaNode: AreaNode
    private lateinit var mainAreas: List<Area>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainAreas = Utils.getMainAreas(requireContext())
        rootAreaNode = AreaNode().apply {
            next = AreaNode(mainAreas, null)
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSample2Binding.inflate(inflater, container, false)
        binding.selectedArea = selectedArea
        binding.chooseButton.setOnClickListener { areaClick() }

        areaPicker.setOnAreaChangeListener(this)

        return binding.root
    }

    private fun areaClick() {

    }

    override fun onAreaChange(level: Int, index: Int) {
        var root: AreaNode? = rootAreaNode
        for (i in 0..level) root = root?.next
    }

    data class AreaNode(
            var areas: List<Area>? = null,
            var next: AreaNode? = null
    )
}