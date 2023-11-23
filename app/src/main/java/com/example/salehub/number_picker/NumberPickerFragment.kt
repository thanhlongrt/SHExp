package com.example.salehub.number_picker

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.salehub.base.BaseViewBindingFragment
import com.example.salehub.databinding.FragmentNumberPickerBinding

class NumberPickerFragment : BaseViewBindingFragment<FragmentNumberPickerBinding>() {

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentNumberPickerBinding? {
        return FragmentNumberPickerBinding.inflate(inflater, container, attachToParent)
    }

    override fun initView() {
        binding?.numberPicker?.run {
            displayedValues = (1..100).toList().map { it.toString() }.toTypedArray()
            minValue = 0
            maxValue = 99
        }
    }
}
