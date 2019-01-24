package me.liangfei.areapicker.sample.utils

import android.content.Context
import me.liangfei.areapicker.sample.data.AreaDataRepository
import me.liangfei.areapicker.sample.viewmodels.AreaViewModelFactory

/**
 * Created by LIANG.FEI on 24/1/2019.
 */

object InjectorUtils {
    fun provideAreaViewModelFactory(context: Context): AreaViewModelFactory {
        val repository = AreaDataRepository(context)
        return AreaViewModelFactory(repository)
    }
}