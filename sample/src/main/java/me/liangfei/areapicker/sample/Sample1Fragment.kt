package me.liangfei.areapicker.sample

import android.database.Observable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableField
import androidx.fragment.app.Fragment
import me.liangfei.areapicker.sample.databinding.FragmentSample1Binding

/**
 * Created by LIANG.FEI on 22/1/2019.
 */
class Sample1Fragment : Fragment() {
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSample1Binding.inflate(inflater, container, false)
        return binding.root
    }
}