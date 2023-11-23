package com.example.salehub.tooltip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.salehub.R
import com.example.salehub.base.BaseViewBindingFragment
import com.example.salehub.databinding.FragmentTooltipBinding
import com.example.salehub.tooltip.TooltipHelper.TooltipView
import com.example.salehub.utils.px

class TooltipFragment : BaseViewBindingFragment<FragmentTooltipBinding>() {
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentTooltipBinding? {
        return FragmentTooltipBinding.inflate(inflater, container, attachToParent)
    }

    var tooltip: TooltipView? = null

    override fun initView() {
        binding?.run {
        }

        binding?.iv?.setOnClickListener {
            showCustomTooltip()
        }

        binding?.root?.setOnClickListener {
            tooltip?.remove()
        }
    }

    private fun showCustomTooltip() {
        if (tooltip != null) return
        var customView: View? =
            layoutInflater.inflate(R.layout.layout_tooltip, binding?.root, false)
        tooltip = TooltipHelper
            .on(binding?.iv!!)
            .customView(customView!!)
            .arrowSourceMargin(0)
            .arrowTargetMargin(0)
            .withShadow(false)
            .distanceWithView(0)
            .padding(8.px, 8.px, 8.px, 8.px)
            .margin(16.px, 0, 16.px, 0)
            .clickToHide(true)
            .autoHide(false, 0)
            .position(TooltipHelper.Position.BOTTOM)
            .align(TooltipHelper.ALIGN.CENTER)
            .onHide(object : TooltipHelper.ListenerHide {
                override fun onHide(view: View?) {
                    (view as? TooltipView)?.removeView(customView)
                    tooltip = null
                    customView = null
                }
            })
            .show()
    }

    override fun onDestroy() {
        tooltip?.removeNow()
        super.onDestroy()
    }
}
