package me.liangfei.areapicker.sample.utils

import android.content.Context

/**
 * Created by LIANG.FEI on 24/1/2019.
 */
object FileUtils {
    fun readAssetFileToString(context: Context, assetFileName: String)
            = context.assets.open(assetFileName).bufferedReader().use { it.readText() }
}