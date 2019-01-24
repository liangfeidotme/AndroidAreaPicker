package me.liangfei.areapicker.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import me.liangfei.areapicker.RFAreaPicker
import me.liangfei.areapicker.sample.databinding.FragmentSample1Binding
import me.liangfei.areapicker.sample.utils.InjectorUtils
import me.liangfei.areapicker.sample.viewmodels.AreaViewModel

/**
 * Created by LIANG.FEI on 22/1/2019.
 */
class Sample1Fragment : Fragment() {
    lateinit var viewmodel: AreaViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSample1Binding.inflate(inflater, container, false)

        val areaDataRepository = InjectorUtils.provideAreaViewModelFactory(requireContext())

        viewmodel = ViewModelProviders.of(this, areaDataRepository)
                .get(AreaViewModel::class.java)

        binding.viewmodel = viewmodel
        binding.chooseAreaClickListener = onPickAreaListener
        return binding.root
    }

    private val onPickAreaListener = View.OnClickListener {
        viewmodel.areaData.observe(viewLifecycleOwner, Observer { areaModel ->
            RFAreaPicker.create(areaModel).apply {
                viewmodel.selectedArea.get()?.let {
                    addressParts = it.split(" ")
                }

                areaPickListener = { area ->
                    viewmodel.selectedArea.set(area.joinToString(" ") { it.name })
                }
            }.show(requireFragmentManager(), "")
        })
    }
}