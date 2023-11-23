package com.example.salehub.tooltip

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import com.example.salehub.R
import com.example.salehub.utils.px
import kotlin.math.roundToInt
import kotlin.math.sqrt

class MyTooltip : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    private var strokeWidth = 2.px.toFloat()
    private val bubblePath = Path()
    private var cornerRadius = 8.px.toFloat()
    private var arrowCenterXCoordinate = 0f
    private var arrowSize = 12.px.toFloat()
    private val arrowHeight: Float
        get() = arrowSize / sqrt(2f)
    private var padding = 8.px
    private var textView: TextView? = null
    private val iconDrawable = ContextCompat.getDrawable(context, R.drawable.ic_notification)
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = this@MyTooltip.strokeWidth
        color = Color.BLACK
    }

    init {
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val heightWithoutCorners = height - cornerRadius * 2 - arrowHeight - strokeWidth
        val widthWithoutCorners = width - cornerRadius * 2 - strokeWidth
        bubblePath.run {
            reset()
            moveTo(cornerRadius + strokeWidth / 2, arrowHeight + strokeWidth / 2)
            rQuadTo(-cornerRadius, 0f, -cornerRadius, cornerRadius)
            rLineTo(0f, heightWithoutCorners)
            rQuadTo(0f, cornerRadius, cornerRadius, cornerRadius)
            rLineTo(widthWithoutCorners, 0f)
            rQuadTo(cornerRadius, 0f, cornerRadius, -cornerRadius)
            rLineTo(0f, -heightWithoutCorners)
            rQuadTo(0f, -cornerRadius, -cornerRadius, -cornerRadius)
            rLineTo(
                -(widthWithoutCorners - arrowCenterXCoordinate - arrowSize / 2 + marginLeft + x - marginRight),
                0f
            )

            // draw arrow
            rLineTo(-arrowSize / 2, -arrowHeight)
            rLineTo(-arrowSize / 2, arrowHeight)
            close()
        }
        canvas?.drawPath(bubblePath, borderPaint)

        iconDrawable?.run {
            setBounds(
                padding,
                padding + arrowHeight.roundToInt(),
                padding + this.intrinsicWidth,
                padding + arrowHeight.roundToInt() + this.intrinsicHeight,
            )
            setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
            canvas?.let(::draw)
        }

        if (textView == null) {
            addTextView()
        }
    }

    private fun addTextView() {
        textView = TextView(context).apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    topMargin = padding
                }
            setTextColor(Color.BLACK)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            text =
                "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ thường, chữ in hoa, số và ký tự đặc biệt. "
//            text = "Mật khẩu "
            addView(this)
        }
        textView?.run {
            x = padding * 2f + (iconDrawable?.intrinsicWidth ?: 0)
            setPadding(0, 0, padding * 2 + (iconDrawable?.intrinsicWidth ?: 0), 0)
        }
    }

    fun setArrowPosition(xCoordinate: Float) {
        arrowCenterXCoordinate = xCoordinate
    }

    class MyTooltipHelper(
        private val tooltip: MyTooltip,
        private val target: View,
    ) {

        private var activityContext: Activity?

        init {
            activityContext = getActivityContext(target.context)
        }

        fun show() {
            if (activityContext == null) return
            val location = IntArray(2)
            target.getLocationOnScreen(location)
            tooltip.setArrowPosition(location[0].toFloat())
            tooltip.viewTreeObserver.addOnPreDrawListener(object :
                    ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        tooltip.y = location[1].toFloat() + target.height
                        tooltip.viewTreeObserver.removeOnPreDrawListener(this)
                        return true
                    }
                })
            ValueAnimator.ofFloat(tooltip.alpha, 1f).apply {
                addUpdateListener {
                    tooltip.alpha = it.animatedValue as Float
                    tooltip.invalidate()
                }
                doOnStart { tooltip.isVisible = true }
                duration = ANIM_DURATION
                start()
            }
        }

        fun hide() {
            ValueAnimator.ofFloat(tooltip.alpha, 0f).apply {
                addUpdateListener {
                    tooltip.alpha = it.animatedValue as Float
                }
                doOnEnd { tooltip.isVisible = false }
                duration = ANIM_DURATION
                start()
            }
        }

        companion object {
            private const val ANIM_DURATION = 200L
            private fun getActivityContext(context: Context): Activity? {
                var activityContext: Context? = context
                while (activityContext is ContextWrapper) {
                    if (activityContext is Activity) {
                        return activityContext
                    }
                    activityContext = activityContext.baseContext
                }
                return null
            }
        }
    }
}
