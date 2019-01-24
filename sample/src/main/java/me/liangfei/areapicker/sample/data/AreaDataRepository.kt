package me.liangfei.areapicker.sample.data

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import me.liangfei.areapicker.RFAreaModel
import me.liangfei.areapicker.sample.App
import me.liangfei.areapicker.sample.utils.FileUtils

/**
 * Area data repository.
 *
 * Created by LIANG.FEI on 24/1/2019.
 */
class AreaDataRepository(val context: Context) {
    fun getAreaData() : MutableLiveData<RFAreaModel> {
        val areasJson = FileUtils.readAssetFileToString(context, "areas.json")
        val areaModel = Gson().fromJson<RFAreaModel>(areasJson, RFAreaModel::class.java)
        return MutableLiveData<RFAreaModel>().apply {
            value = areaModel
        }
    }
}