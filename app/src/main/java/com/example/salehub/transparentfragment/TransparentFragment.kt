package com.example.salehub.transparentfragment

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Toast
import com.example.salehub.R
import com.example.salehub.base.BaseViewBindingFragment
import com.example.salehub.databinding.FragmentTransparentBinding

class TransparentFragment : BaseViewBindingFragment<FragmentTransparentBinding>() {

    override fun initView() {
        binding?.frameLayoutMain?.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                dismiss()
            }
            v.performClick()
        }
        binding?.fabRed?.setOnClickListener {
            dismiss()
        }

        binding?.fabBlue?.setOnClickListener {
            Toast.makeText(context, "BLUE", Toast.LENGTH_SHORT).show()
        }

        binding?.fabYellow?.setOnClickListener {
            Toast.makeText(context, "YELLOW", Toast.LENGTH_SHORT).show()
        }
        animateButtons()
    }

    private fun animateButtons() {
        val container = binding?.fabBlue?.parent as ViewGroup
        val containerH = container.height.toFloat()
        val fabRedH = binding?.fabRed?.height ?: 0
        val spacing = resources.getDimensionPixelSize(R.dimen._20dp)
        val blueAnimator = ObjectAnimator.ofFloat(
            binding?.fabBlue,
            View.TRANSLATION_Y,
            containerH - spacing,
            containerH - spacing - fabRedH - spacing - spacing
        )
        blueAnimator.interpolator = LinearInterpolator()
        blueAnimator.duration = 1000
        blueAnimator.start()
    }

    fun dismiss() {
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentTransparentBinding? {
        return FragmentTransparentBinding.inflate(inflater, container, false)
    }
}