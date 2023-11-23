package com.example.salehub.choose_address_view

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.salehub.base.BaseViewBindingFragment
import com.example.salehub.databinding.FragmentChooseAddressBinding

class ChooseAddressFragment : BaseViewBindingFragment<FragmentChooseAddressBinding>() {
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentChooseAddressBinding? {
        return FragmentChooseAddressBinding.inflate(inflater, container, attachToParent)
    }

    override fun initView() {
        binding?.chooseAddressView?.displayedValues = mutableListOf("Ha Noi", "Hai Bà Trưng")
        binding?.btnAdd?.setOnClickListener {
            binding?.chooseAddressView?.addItem("New Item")
        }
    }
}
