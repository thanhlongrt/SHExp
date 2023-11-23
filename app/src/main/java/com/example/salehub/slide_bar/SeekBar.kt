package com.example.salehub.slide_bar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.salehub.R
import com.example.salehub.databinding.LayoutSeekbarBinding
import kotlin.math.roundToInt

class SeekBar : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    var binding: LayoutSeekbarBinding =
        LayoutSeekbarBinding.inflate(LayoutInflater.from(context), this, true)

    var maxValue = 100f
    var minValue = 0f

    var progress = 0f

    val spacingProgressHorizontal = resources.getDimensionPixelOffset(R.dimen._30dp)

    val paddingHorizontal = resources.getDimensionPixelOffset(R.dimen._16dp)

    val thumbWidth = resources.getDimensionPixelOffset(R.dimen._60dp)

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                binding.bubbleView.isVisible = true
                updateProgress(event.x)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                updateProgress(event.x)
                true
            }
            MotionEvent.ACTION_UP -> {
                binding.bubbleView.isInvisible = true
                true
            }
            MotionEvent.ACTION_CANCEL -> {
                binding.bubbleView.isInvisible = true
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    private fun updateProgress(x: Float) {
        when {
            x <= spacingProgressHorizontal + binding.tvMinValue.width + paddingHorizontal -> {
                progress = minValue
                binding.guideline.setGuidelinePercent((spacingProgressHorizontal + binding.tvMinValue.width + paddingHorizontal).toFloat() / width)
            }
            x >= width - (spacingProgressHorizontal + binding.tvMaxValue.width + paddingHorizontal) -> {
                progress = maxValue
                binding.guideline.setGuidelinePercent((width - spacingProgressHorizontal - paddingHorizontal - binding.tvMaxValue.width).toFloat() / width)
            }
            else -> {
                progress =
                    (maxValue - minValue) * (x - spacingProgressHorizontal - binding.tvMinValue.width - paddingHorizontal) / (width - spacingProgressHorizontal * 2 - paddingHorizontal * 2 - binding.tvMinValue.width - binding.tvMaxValue.width)
                binding.guideline.setGuidelinePercent(x / width)
            }
        }

        binding.tvProgress.text = progress.roundToInt().toString()

        binding.bubbleView.setContentString(progress.roundToInt().toString())
    }
}