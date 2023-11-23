package com.example.salehub.ruler_picker

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.salehub.R
import com.example.salehub.base.BaseViewBindingFragment
import com.example.salehub.databinding.FragmentRulerPickerBinding

class RulerPickerFragment : BaseViewBindingFragment<FragmentRulerPickerBinding>(),
    RulerValuePickerListener {

    override fun initView() {
        binding?.rulerView?.setValuePickerListener(this)
        binding?.rulerView?.setDisplayValues(
            listOf(
                "00:15",
                "00:30",
                "01:00",
                "01:15",
                "01:30",
                "01:45",
                "02:00"
            )
        )
        binding?.rulerView?.selectValue(0)
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentRulerPickerBinding {
        return FragmentRulerPickerBinding.inflate(inflater, container, false)
    }

    override fun onValueChange(selectedValue: Int) {
        Log.e("TAG", "onValueChange: $selectedValue")
    }

    override fun onIntermediateValueChange(currentValue: Float) {
        Log.e("TAG", "onIntermediateValueChange: $currentValue")
    }


}