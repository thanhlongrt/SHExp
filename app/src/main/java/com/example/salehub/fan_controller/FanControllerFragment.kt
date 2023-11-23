package com.example.salehub.fan_controller

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.salehub.R
import com.example.salehub.base.BaseViewBindingFragment
import com.example.salehub.databinding.FragmentFanControllerBinding

class FanControllerFragment : BaseViewBindingFragment<FragmentFanControllerBinding>() {

    override fun initView() {
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentFanControllerBinding? {
        return FragmentFanControllerBinding.inflate(inflater, container, false)
    }
}