package com.example.salehub.choose_address_view

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import com.example.salehub.R

class ChooseAddressView : View {

    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, styleRes: Int) : super(
        context,
        attributeSet,
        styleRes
    )

    private val currentStepTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.primary_100)
        textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            DEFAULT_TEXT_SIZE, resources.displayMetrics
        )
        textAlign = Paint.Align.CENTER
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.other_success)
        textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            DEFAULT_TEXT_SIZE, resources.displayMetrics
        )
        textAlign = Paint.Align.CENTER
    }

    private val indicatorBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.primary_10)
    }

    private val indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.primary_60)
    }

    var indicatorHeight = resources.getDimensionPixelSize(R.dimen._4dp).toFloat()

    var indicatorRadius = resources.getDimensionPixelSize(R.dimen._2dp).toFloat()

    var maxStep = DEFAULT_MAX_STEP

    var stepValues = mutableListOf("Chọn Tỉnh/ TP", "Chọn Quận/ Huyện", "Chọn Xã/ Phường")

    private val rects = mutableListOf<Rect>()

    private val valueXCoordinates = mutableListOf<Float>()

    var currentStep = 0

    var stepTemp = 0f

    var displayedValues = mutableListOf("Ha Ha")
        set(value) {
            if (value.size < maxStep) {
                value.add(stepValues[value.size])
            }
            field = value
            calculateRects()
            ValueAnimator.ofFloat(currentStep.toFloat(), value.lastIndex.toFloat()).apply {
                addUpdateListener {
                    stepTemp = it.animatedValue as Float
                    postInvalidateOnAnimation()
                }
                duration = INDICATOR_ANIM_DURATION
                doOnEnd {
                    currentStep = value.lastIndex
                    stepTemp = currentStep.toFloat()
                }
                interpolator = AccelerateInterpolator()
                start()
            }
        }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        calculateXCoordinates()
    }

    override fun onDraw(canvas: Canvas?) {
        drawIndicatorBackground(canvas)
        drawIndicator(canvas, stepTemp)
        drawTexts(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                checkClickedStep(event.x)?.let { newStep ->
                    this@ChooseAddressView.displayedValues =
                        this@ChooseAddressView.displayedValues.subList(0, newStep)
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun calculateXCoordinates() {
        valueXCoordinates.clear()
        for (index in 0 until maxStep) {
            val x = width.toFloat() / maxStep * index + width.toFloat() / maxStep / 2f
            valueXCoordinates.add(x)
        }
    }

    private fun calculateRects() {
        rects.clear()
        displayedValues.forEachIndexed { _, value ->
            val rect = Rect()
            textPaint.getTextBounds(value, 0, value.lastIndex, rect)
            rects.add(rect)
        }
    }

    private fun drawIndicatorBackground(canvas: Canvas?) {
        canvas?.drawRoundRect(
            0f,
            height.toFloat() - indicatorHeight,
            width.toFloat(),
            height.toFloat(),
            indicatorRadius,
            indicatorRadius,
            indicatorBackgroundPaint
        )
    }

    private fun drawIndicator(canvas: Canvas?, step: Float) {
        canvas?.drawRoundRect(
            width.toFloat() / maxStep * step,
            height.toFloat() - indicatorHeight,
            width.toFloat() / maxStep * (step + 1),
            height.toFloat(),
            indicatorRadius,
            indicatorRadius,
            indicatorPaint
        )
    }

    private fun drawTexts(canvas: Canvas?) {
        displayedValues.forEachIndexed { index, text ->
            val paint = if (index < currentStep) textPaint else currentStepTextPaint
            if (index > currentStep || index > stepTemp) return@forEachIndexed
            canvas?.drawText(
                text,
                valueXCoordinates[index],
                height / 2f + rects[index].height() / 2f,
                paint
            )
        }
    }

    private fun checkClickedStep(x: Float): Int? {
        valueXCoordinates.forEachIndexed { index, xCoordinate ->
            if (x in (xCoordinate - width.toFloat() / maxStep / 2f)..(xCoordinate + width.toFloat() / maxStep / 2f) &&
                index <= currentStep
            ) {
                return index
            }
        }
        return null
    }

    fun addItem(item: String) {
        if (currentStep >= maxStep - 1) return
        displayedValues = displayedValues.subList(0, currentStep).apply { add(item) }
    }

    companion object {
        private const val DEFAULT_TEXT_SIZE = 14F
        private const val DEFAULT_MAX_STEP = 3
        private const val INDICATOR_ANIM_DURATION = 100L
    }
}
