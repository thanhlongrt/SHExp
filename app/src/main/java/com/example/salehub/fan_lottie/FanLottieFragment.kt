package com.example.salehub.fan_lottie

import android.animation.Animator
import android.view.LayoutInflater
import android.view.ViewGroup
import com.airbnb.lottie.LottieDrawable
import com.example.salehub.base.BaseViewBindingFragment
import com.example.salehub.databinding.FragmentFanLottieBinding

class FanLottieFragment : BaseViewBindingFragment<FragmentFanLottieBinding>() {

    override fun initView() {
        binding?.animationView?.setAnimation(ANIM_LINEAR)
        initActions()
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentFanLottieBinding? {
        return FragmentFanLottieBinding.inflate(inflater, container, false)
    }

    private fun initActions() {
        binding?.run {
            btnCancel.setOnClickListener {
                cancelAnimation()
            }
            btnStart.setOnClickListener {
                animateFanStart()
            }
            btnStop.setOnClickListener {
                animateFanStop()
            }
        }
    }

    private fun playAnimation(mode: FanMode, repeatCount: Int, onEnd: (() -> Unit)? = null) {
        binding?.animationView?.run {
            if (isAnimating) cancelAnimation()
            when (mode) {
                FanMode.LINEAR -> {
                    setAnimation(ANIM_LINEAR)
                    this.repeatCount = repeatCount
                }
                FanMode.ACCELERATE -> {
                    this.repeatCount = 0
                    setAnimation(ANIM_ACCELERATE)
                }
                FanMode.DECELERATE -> {
                    this.repeatCount = 0
                    setAnimation(ANIM_DECELERATE)
                }
            }
            addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {
                    onEnd?.invoke()
                    removeAllAnimatorListeners()
                }

                override fun onAnimationCancel(animation: Animator?) {
                    removeAllAnimatorListeners()
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }

            })

            playAnimation()

        }
    }

    private fun animateFanStart() {
        playAnimation(FanMode.ACCELERATE, 0) {
            playAnimation(FanMode.LINEAR, LottieDrawable.INFINITE)
        }
    }

    private fun animateFanStop() {
        playAnimation(FanMode.LINEAR, 1) {
            playAnimation(FanMode.DECELERATE, 0) {

            }
        }
    }

    private fun cancelAnimation() {
        binding?.animationView?.cancelAnimation()
    }

    companion object {
        const val ANIM_LINEAR = "fan_animation_linear.json"
        const val ANIM_ACCELERATE = "fan_animation_accelerate.json"
        const val ANIM_DECELERATE = "fan_animation_decelerate.json"
    }

    enum class FanMode {
        LINEAR, ACCELERATE, DECELERATE
    }
}