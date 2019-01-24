package me.liangfei.areapicker.sample.viewmodels

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import me.liangfei.areapicker.sample.data.AreaDataRepository

/**
 * Created by LIANG.FEI on 23/1/2019.
 */
class AreaViewModel internal constructor(repository: AreaDataRepository): ViewModel() {
    val selectedArea = ObservableField<String>()
    val areaData = repository.getAreaData()
}