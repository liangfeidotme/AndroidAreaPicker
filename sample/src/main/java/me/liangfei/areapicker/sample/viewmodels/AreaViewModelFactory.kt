package me.liangfei.areapicker.sample.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.liangfei.areapicker.sample.data.AreaDataRepository

/**
 * Created by LIANG.FEI on 24/1/2019.
 */
@Suppress("UNCHECKED_CAST")
class AreaViewModelFactory(
        private val areaDataRepository: AreaDataRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AreaViewModel(areaDataRepository) as T
    }
}