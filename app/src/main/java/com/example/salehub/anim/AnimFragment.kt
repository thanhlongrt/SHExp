package com.example.salehub.anim

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import com.example.salehub.R
import com.example.salehub.base.BaseViewBindingFragment
import com.example.salehub.databinding.FragmentAnimBinding
import kotlin.math.abs
import kotlin.math.roundToInt

class AnimFragment : BaseViewBindingFragment<FragmentAnimBinding>() {

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentAnimBinding? {
        return FragmentAnimBinding.inflate(inflater, container, false)
    }

    override fun initView() {
        binding?.btnStart?.setOnClickListener {
            val from = binding?.textView?.text?.toString()?.toInt()
            val to = if (from == 1) 100 else 1
            startAnimation(from!!, to)
        }
    }

    private fun startAnimation(from: Int, to: Int) {
        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.addUpdateListener {

//            Log.e(TAG, "startAnimation: ${it.animatedValue}")
//            binding?.bar?.layoutParams?.let { layoutParams ->
//                layoutParams.height = it.animatedValue as Int
//                binding?.bar?.layoutParams = layoutParams
//            }
            val animatedValue = it.animatedValue as Float
            binding?.textView?.text =
                (from + animatedValue * (to - from)).roundToInt().toString()
            binding?.textView?.alpha =
                if (animatedValue < 0.5) (1f - animatedValue)*(1f - animatedValue)
                else animatedValue*animatedValue
        }
        animator.duration = 1000
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.start()
    }
}