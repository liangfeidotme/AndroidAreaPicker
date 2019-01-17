package com.liangfeizc.areapicker.sample

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.liangfei.areapicker.RFAreaModel
import me.liangfei.areapicker.RFAreaPicker

import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView

import com.google.gson.Gson
import com.liangfeizc.areapicker.tb.AreaPicker
import com.liangfeizc.areapicker.zui.AreaModel
import com.liangfeizc.areapicker.utils.FileUtils
import com.liangfeizc.areapicker.zui.ZanAreaPicker

import java.util.ArrayList
import java.util.Arrays


class MainActivity : AppCompatActivity(), AreaPicker.OnAreaChangeListener {
    private var areaPicker: AreaPicker? = null
    private var areaPickerWindow: PopupWindow? = null

    private var mainAreas: List<Area>? = null
    private var rootAreaNode: AreaNode? = null

    private var addressView: TextView? = null
    private lateinit var ktAddressView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addressView = findViewById<View>(R.id.area) as TextView
        addressView!!.setOnClickListener { v -> zuiAreaClick(v) }

        mainAreas = Utils.getMainAreas(this)
        rootAreaNode = AreaNode()
        rootAreaNode!!.next = AreaNode(mainAreas, null)

        ktAddressView = findViewById(R.id.ktAddress)
        ktAddressView.setOnClickListener { showAreaPickerKt() }
    }

    fun areaClick(view: View) {
        if (areaPickerWindow == null) {
            areaPicker = createAreaPicker()
            areaPicker!!.setOnAreaChangeListener(this)
            areaPickerWindow = PopupWindow(areaPicker,
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            areaPickerWindow!!.isOutsideTouchable = true
            areaPickerWindow!!.setBackgroundDrawable(ColorDrawable(0xff))
            areaPickerWindow!!.animationStyle = android.R.style.Animation_Dialog
            areaPickerWindow!!.isFocusable = true
            areaPickerWindow!!.isTouchable = true
        }
        areaPicker!!.setData(Utils.extractNames(mainAreas!!))
        areaPickerWindow!!.showAsDropDown(view)
    }

    fun createAreaPicker(): AreaPicker {
        val picker = AreaPicker(this)
        picker.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        return picker
    }

    override fun onAreaChange(level: Int, index: Int) {
        Log.d("RFAreaPicker", "level:$level,index:$index")
        var root = rootAreaNode
        for (i in 0..level) {
            root = root!!.next
        }

        val id = root!!.areas!![index].id
        val subAreas = Utils.getSubAreasById(this, id)
        if (subAreas != null) {
            root.next = AreaNode(subAreas, null)
            areaPicker!!.setData(Utils.extractNames(subAreas), 0, level + 1)
        }
    }

    private fun zuiAreaClick(view: View) {
        val jsonStr = FileUtils.readAssetFileToString(this, "areas.json")
        val areaModel = Gson().fromJson<AreaModel>(jsonStr, AreaModel::class.java!!)
        val selAreas = Arrays.asList<String>(*addressView!!.text.toString().split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray())
        val areaPicker = ZanAreaPicker.create(areaModel)
        areaPicker.setAddressParts(selAreas)
        areaPicker.setOnPickAreaListener { areas ->
            val names = ArrayList<String>(areas.size)
            for (area in areas) {
                names.add(area.name)
            }
            addressView!!.text = TextUtils.join(" ", names)
        }
        areaPicker.show(supportFragmentManager, "")
    }

    // Kotlin version
    private fun showAreaPickerKt() {
        val areasJson = FileUtils.readAssetFileToString(this, "areas.json")
        val areaModel = Gson().fromJson<RFAreaModel>(areasJson, RFAreaModel::class.java)
        val areaPicker = RFAreaPicker.create(areaModel)

        ktAddressView.text.toString().trim().let {
            if (it.isNotEmpty()) {
                areaPicker.addressParts = it.split(" ")
            }
        }
        areaPicker.areaPickListener = { area ->
            ktAddressView.text = area.joinToString(" ") { it.name }
        }
        areaPicker.show(supportFragmentManager, "")
    }

    inner class AreaNode @JvmOverloads constructor(var areas: List<Area>? = null, var next: AreaNode? = null)
}
