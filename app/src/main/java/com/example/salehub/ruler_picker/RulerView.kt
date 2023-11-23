/*
 * Copyright 2018 Keval Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance wit
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 *  the specific language governing permissions and limitations under the License.
 */
package com.example.salehub.ruler_picker

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.Dimension
import androidx.annotation.FloatRange
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.graphics.ColorUtils
import com.example.salehub.R
import com.kevalpatel2106.rulerpicker.RulerViewUtils
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Created by Keval Patel on 28 Mar 2018.
 *
 *
 * This is custom [View] which will draw a ruler with indicators.
 * There are two types of indicators:
 *  * **Long Indicators:** These indicators marks specific important value after some periodic interval.
 * e.g. Long indicator represents evert 10th (10, 20, 30...) value.
 *  * **Short Indicators:** There indicators represents single value.
 */
internal class RulerView : View {

    companion object {
        const val DEFAULT_TEXT_SIZE = 14
        const val DEFAULT_SELECTED_TEXT_SIZE = 28
        const val DEFAULT_TEXT_MARGIN_BOTTOM = 8
        const val DEFAULT_INDICATOR_MIN_WIDTH = 3F
        const val DEFAULT_INDICATOR_MAX_WIDTH = 6F
        const val DEFAULT_INDICATOR_RADIUS = 8
        const val DEFAULT_SELECTED_INDICATOR_HEIGHT = 60
        const val DEFAULT_INDICATOR_HEIGHT = 30
        const val MULTIPLIER = 10000
    }

    /**
     * Height of the view. This view height is measured in [.onMeasure].
     *
     * @see .onMeasure
     */
    private var mViewHeight = 0

    /**
     * [Paint] for the line in the ruler view.
     *
     * @see .refreshPaint
     */
    private var mIndicatorPaint: Paint? = null

    private var mSelectedIndicatorPaint: Paint? = null


    /**
     * [Paint] to display the text on the ruler view.
     *
     * @see .refreshPaint
     */
    private var mTextPaint: Paint? = null
    private var mSelectedTextPaint: Paint? = null
    /**
     * @return Get distance between two indicator in pixels.
     * @see .setIndicatorIntervalDistance
     */
    /**
     * Distance interval between two subsequent indicators on the ruler.
     *
     * @see .setIndicatorIntervalDistance
     * @see .getIndicatorIntervalWidth
     */
    @get:CheckResult
    var indicatorIntervalWidth = 14 * 5 /* Default value */
        private set

    /**
     * @return Get the minimum value displayed on the ruler.
     * @see .setValueRange
     */
    /**
     * Minimum value. This value will be displayed at the left-most end of the ruler. This value
     * must be less than [.mMaxValue].
     *
     * @see .setValueRange
     * @see .getMinValue
     */
    @get:CheckResult
    var minValue = 0 /* Default value */
        private set
    /**
     * @return Get the maximum value displayed on the ruler.
     * @see .setValueRange
     */
    /**
     * Maximum value. This value will be displayed at the right-most end of the ruler. This value
     * must be greater than [.mMinValue].
     *
     * @see .setValueRange
     * @see .getMaxValue
     */
    @get:CheckResult
    var maxValue = 100 /* Default maximum value */
        private set

    var currentValue = 0f
        set(value) {
            field = value
            invalidate()
        }

    var selectedValue = 0
        set(value) {
            field = value
            invalidate()
        }
    /**
     * @return Ratio of long indicator height to the ruler height.
     * @see .setIndicatorHeight
     */
    /**
     * Ratio of long indicator height to the ruler height. This value must be between 0 to 1. The
     * value should greater than [.mShortIndicatorHeight]. Default value is 0.6 (i.e. 60%).
     * If the value is 0, indicator won't be displayed. If the value is 1, indicator height will be
     * same as the ruler height.
     *
     * @see .setIndicatorHeight
     * @see .getLongIndicatorHeightRatio
     */
    @get:CheckResult
    var longIndicatorHeightRatio = 0.3f /* Default value */
        private set

    @get:CheckResult
    var selectedIndicatorHeightRatio = 0.6f
        private set
    /**
     * @return Ratio of short indicator height to the ruler height.
     * @see .setIndicatorHeight
     */
    /**
     * Ratio of short indicator height to the ruler height. This value must be between 0 to 1. The
     * value should less than [.mLongIndicatorHeight]. Default value is 0.4 (i.e. 40%).
     * If the value is 0, indicator won't be displayed. If the value is 1, indicator height will be
     * same as the ruler height.
     *
     * @see .setIndicatorHeight
     * @see .getShortIndicatorHeightRatio
     */
    @get:CheckResult
    var shortIndicatorHeightRatio = 0.3f /* Default value */
        private set

    /**
     * Actual height of the long indicator in pixels. This height is derived from
     * [.mLongIndicatorHeightRatio].
     *
     * @see .updateIndicatorHeight
     */
    private var mLongIndicatorHeight = 0

    var mSelectedIndicatorHeight = 0
        set(value) {
            field = value
            invalidate()
        }

    /**
     * Actual height of the short indicator in pixels. This height is derived from
     * [.mShortIndicatorHeightRatio].
     *
     * @see .updateIndicatorHeight
     */
    var mIndicatorHeight = 0
        set(value) {
            field = value
            invalidate()
        }

    /**
     * Integer color of the text, that is displayed on the ruler.
     *
     * @see .setTextColor
     * @see .getTextColor
     */
    @ColorInt
    private var mTextColor = Color.WHITE

    private var mSelectedTextColor = Color.GREEN

    /**
     * Integer color of the indicators.
     *
     * @see .setIndicatorColor
     * @see .getIndicatorColor
     */
    @ColorInt
    private var mIndicatorColor = Color.GRAY

    @ColorInt
    private var mSelectedIndicatorColor = Color.GREEN

    /**
     * Height of the text, that is displayed on ruler in pixels.
     *
     * @see .setTextSize
     * @see .getTextSize
     */
    @Dimension
    private var mTextSize = 14

    var mSelectedTextSize = 28

    var mTextMarginBottom = DEFAULT_TEXT_MARGIN_BOTTOM
    /**
     * @return Width of the indicator in pixels.
     * @see .setIndicatorWidth
     */
    /**
     * Width of the indicator in pixels.
     *
     * @see .setIndicatorWidth
     * @see .getIndicatorWidth
     */
    @get:CheckResult
    @Dimension
    var indicatorMaxWidth = DEFAULT_INDICATOR_MAX_WIDTH
        private set

    var indicatorMinWidth = DEFAULT_INDICATOR_MIN_WIDTH
        private set

    var mIndicatorRadius = DEFAULT_INDICATOR_RADIUS

    constructor(@NonNull context: Context?) : super(context) {
        parseAttr(null)
    }

    constructor(
        @NonNull context: Context?,
        @Nullable attrs: AttributeSet?
    ) : super(context, attrs) {
        parseAttr(attrs)
    }

    constructor(
        @NonNull context: Context?,
        @Nullable attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        parseAttr(attrs)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        @NonNull context: Context?,
        @Nullable attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        parseAttr(attrs)
    }

    private fun parseAttr(@Nullable attributeSet: AttributeSet?) {
        if (attributeSet != null) {
            val a = context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.RulerView,
                0,
                0
            )
            try { //Parse params
                if (a.hasValue(R.styleable.RulerView_ruler_text_color)) {
                    mTextColor = a.getColor(R.styleable.RulerView_ruler_text_color, Color.WHITE)
                }
                if (a.hasValue(R.styleable.RulerView_ruler_selected_text_color)) {
                    mSelectedTextColor =
                        a.getColor(R.styleable.RulerView_ruler_selected_text_color, Color.GREEN)
                }
                if (a.hasValue(R.styleable.RulerView_ruler_text_size)) {
                    mTextSize = a.getDimensionPixelSize(
                        R.styleable.RulerView_ruler_text_size,
                        DEFAULT_TEXT_SIZE
                    )
                }
                if (a.hasValue(R.styleable.RulerView_ruler_selected_text_size)) {
                    mSelectedTextSize =
                        a.getDimensionPixelSize(
                            R.styleable.RulerView_ruler_selected_text_size,
                            DEFAULT_SELECTED_TEXT_SIZE
                        )
                }
                if (a.hasValue(R.styleable.RulerView_ruler_text_margin_bottom)) {
                    mTextMarginBottom =
                        a.getDimensionPixelSize(
                            R.styleable.RulerView_ruler_text_margin_bottom,
                            DEFAULT_TEXT_MARGIN_BOTTOM
                        )
                }
                if (a.hasValue(R.styleable.RulerView_indicator_color)) {
                    mIndicatorColor = a.getColor(R.styleable.RulerView_indicator_color, Color.WHITE)
                }
                if (a.hasValue(R.styleable.RulerView_indicator_max_width)) {
                    indicatorMaxWidth = a.getDimensionPixelSize(
                        R.styleable.RulerView_indicator_max_width,
                        DEFAULT_INDICATOR_MAX_WIDTH.toInt()
                    ).toFloat()
                }
                if (a.hasValue(R.styleable.RulerView_indicator_min_width)) {
                    indicatorMinWidth = a.getDimensionPixelSize(
                        R.styleable.RulerView_indicator_min_width,
                        DEFAULT_INDICATOR_MIN_WIDTH.toInt()
                    ).toFloat()
                }

                if (a.hasValue(R.styleable.RulerView_indicator_interval)) {
                    indicatorIntervalWidth = a.getDimensionPixelSize(
                        R.styleable.RulerView_indicator_interval,
                        4
                    )
                }
                mSelectedIndicatorHeight = a.getDimensionPixelSize(
                    R.styleable.RulerView_selected_indicator_height,
                    DEFAULT_SELECTED_INDICATOR_HEIGHT
                )
                mIndicatorHeight = a.getDimensionPixelSize(
                    R.styleable.RulerView_selected_indicator_height,
                    DEFAULT_INDICATOR_HEIGHT
                )
//                if (a.hasValue(R.styleable.RulerView_long_height_height_ratio)) {
//                    longIndicatorHeightRatio = a.getFraction(
//                        R.styleable.RulerView_long_height_height_ratio,
//                        1, 1, 0.6f
//                    )
//                }
//                if (a.hasValue(R.styleable.RulerView_short_height_height_ratio)) {
//                    shortIndicatorHeightRatio = a.getFraction(
//                        R.styleable.RulerView_short_height_height_ratio,
//                        1, 1, 0.3f
//                    )
//                }
//                setIndicatorHeight(longIndicatorHeightRatio, shortIndicatorHeightRatio)
                if (a.hasValue(R.styleable.RulerView_min_value)) {
                    minValue = a.getInteger(R.styleable.RulerView_min_value, 0)
                }
                if (a.hasValue(R.styleable.RulerView_max_value)) {
                    maxValue = a.getInteger(R.styleable.RulerView_max_value, 100)
                }
                setValueRange(minValue, maxValue)
            } finally {
                a.recycle()
            }
        }
        refreshPaint()
    }

    /**
     * Create the indicator paint and value text color.
     */
    private fun refreshPaint() {
        mIndicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mIndicatorPaint!!.color = mIndicatorColor
        mIndicatorPaint!!.strokeWidth = indicatorMaxWidth
        mIndicatorPaint!!.style = Paint.Style.STROKE

        mSelectedIndicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mSelectedIndicatorPaint!!.color = mIndicatorColor
        mSelectedIndicatorPaint!!.strokeWidth = indicatorMaxWidth
        mSelectedIndicatorPaint!!.style = Paint.Style.STROKE
        mSelectedIndicatorPaint!!.color = Color.BLACK

        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint!!.color = mTextColor
        mTextPaint!!.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            mTextSize.toFloat(),
            resources.displayMetrics
        )
        mTextPaint!!.textAlign = Paint.Align.CENTER

        mSelectedTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mSelectedTextPaint!!.color = mSelectedTextColor
        mSelectedTextPaint!!.textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            mSelectedTextSize.toFloat(),
            resources.displayMetrics
        )
        mSelectedTextPaint!!.textAlign = Paint.Align.CENTER
        invalidate()
        requestLayout()
    }

    override fun onDraw(canvas: Canvas) {
        //Iterate through all value
        for (value in (currentValue.roundToInt() - 10)..(currentValue.roundToInt() + 10)) {
//            if (value == selectedValue) {
//                drawSelectedIndicator(canvas, value)
//            } else {
//                drawLongIndicator(canvas, value)
//
//            }
            drawIndicator(canvas, value)
            drawValueText(canvas, value)

        }

        //Draw the first indicator.
//        drawSmallIndicator(canvas, 0)

        //Draw the last indicator.
//        drawSmallIndicator(canvas, width)
        super.onDraw(canvas)
    }

    private var mViewWidth: Int = 0

    private var mSelectedTextRect = Rect()


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //Measure dimensions
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec)
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec)
        indicatorIntervalWidth = mViewWidth / 6 * 2
        val desireWidth = (maxValue - minValue) * indicatorIntervalWidth * MULTIPLIER
        val desireHeight = RulerViewUtils.sp2px(
            context,
            mSelectedTextSize.toFloat()
        ) * 1.5 + mTextMarginBottom + mSelectedIndicatorHeight + mIndicatorRadius
        mViewHeight = desireHeight.toInt()
//        mIndicatorHeight = (mViewHeight * shortIndicatorHeightRatio).toInt()
//        mSelectedIndicatorHeight = (mViewHeight * selectedIndicatorHeightRatio).toInt()
        setMeasuredDimension(desireWidth, mViewHeight)

        mSelectedTextPaint?.getTextBounds(
            maxValue.toString(),
            0,
            maxValue.toString().lastIndex,
            mSelectedTextRect
        )
    }

//    /**
//     * Calculate and update the height of the long and the short indicators based on new ratios.
//     *
//     * @param longIndicatorHeightRatio  Ratio of long indicator height to the ruler height.
//     * @param shortIndicatorHeightRatio Ratio of short indicator height to the ruler height.
//     */
//    private fun updateIndicatorHeight(
//        longIndicatorHeightRatio: Float,
//        shortIndicatorHeightRatio: Float
//    ) {
//        mLongIndicatorHeight = (mViewHeight * longIndicatorHeightRatio).toInt()
//        mIndicatorHeight = (mViewHeight * shortIndicatorHeightRatio).toInt()
//    }

    /**
     * Draw the vertical short line at every value.
     *
     * @param canvas [Canvas] on which the line will be drawn.
     * @param value  Value to calculate the position of the indicator.
     */
    private fun drawSmallIndicator(
        @NonNull canvas: Canvas,
        value: Int
    ) {
        canvas.drawLine(
            (indicatorIntervalWidth * value).toFloat(), 0f, (
                    indicatorIntervalWidth * value).toFloat(),
            mIndicatorHeight.toFloat(),
            mIndicatorPaint!!
        )
    }

//    /**
//     * Draw the vertical long line.
//     *
//     * @param canvas [Canvas] on which the line will be drawn.
//     * @param value  Value to calculate the position of the indicator.
//     */
//    private fun drawLongIndicator(
//        @NonNull canvas: Canvas,
//        value: Int
//    ) {
//        canvas.drawLine(
//            (indicatorIntervalWidth * value).toFloat(), 0f, (
//                    indicatorIntervalWidth * value).toFloat(),
//            mLongIndicatorHeight.toFloat(),
//            mIndicatorPaint!!
//        )
//    }

//    private fun drawSelectedIndicator(
//        canvas: Canvas,
//        value: Int
//    ) {
//        canvas.drawLine(
//            (indicatorIntervalWidth * value).toFloat(), 0f, (
//                    indicatorIntervalWidth * value).toFloat(),
//            mSelectedIndicatorHeight.toFloat(),
//            mSelectedIndicatorPaint!!
//        )
//    }

    private fun drawIndicator(
        canvas: Canvas,
        value: Int
    ) {
        val ratio = (1 - (abs(value - currentValue)).coerceIn(0f, 1f))

        mIndicatorPaint!!.apply {
            shader = LinearGradient(
                (indicatorIntervalWidth * value).toFloat() - (indicatorMinWidth + (indicatorMaxWidth - indicatorMinWidth) * ratio) / 2 + indicatorIntervalWidth / 2,
                mViewHeight - ((mSelectedIndicatorHeight - mIndicatorHeight) * ratio + mIndicatorHeight) - mIndicatorRadius,
                (indicatorIntervalWidth * value).toFloat() + (indicatorMinWidth + (indicatorMaxWidth - indicatorMinWidth) * ratio) / 2 + indicatorIntervalWidth / 2,
                mViewHeight.toFloat() - mIndicatorRadius,
                getIndicatorTopColor(
                    (1 - (abs(value - currentValue)).coerceIn(
                        0f,
                        1f
                    ))
                ),
                getIndicatorBottomColor(
                    (1 - (abs(value - currentValue)).coerceIn(
                        0f,
                        1f
                    ))
                ),
                Shader.TileMode.MIRROR
            )
        }

        canvas.drawRoundRect(
            (indicatorIntervalWidth * value).toFloat() - (indicatorMinWidth + (indicatorMaxWidth - indicatorMinWidth) * ratio) / 2 + indicatorIntervalWidth / 2,
            mViewHeight - ((mSelectedIndicatorHeight - mIndicatorHeight) * ratio + mIndicatorHeight) - mIndicatorRadius,
            (indicatorIntervalWidth * value).toFloat() + (indicatorMinWidth + (indicatorMaxWidth - indicatorMinWidth) * ratio) / 2 + indicatorIntervalWidth / 2,
            mViewHeight.toFloat() - mIndicatorRadius,
            mIndicatorRadius.toFloat(),
            mIndicatorRadius.toFloat(),
            mIndicatorPaint!!
        )
    }

    private fun getIndicatorTopColor(@FloatRange(from = 0.0, to = 1.0) ratio: Float): Int {
        return ColorUtils.blendARGB(mIndicatorColor, mSelectedIndicatorColor, ratio)
    }

    private fun getIndicatorBottomColor(@FloatRange(from = 0.0, to = 1.0) ratio: Float): Int {
        return ColorUtils.blendARGB(mIndicatorColor, Color.WHITE, ratio)
    }

    /**
     * Draw the value number below the longer indicator. This will use [.mTextPaint] to draw
     * the text.
     *
     * @param canvas [Canvas] on which the text will be drawn.
     * @param value  Value to draw.
     */
    private fun drawValueText(
        @NonNull canvas: Canvas,
        value: Int
    ) {
        mTextPaint!!.apply {
            color = getIndicatorTopColor(
                (1 - (abs(value - currentValue)).coerceIn(
                    0f,
                    1f
                ))
            )
            textSize =
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_SP,
                    (1 - (abs(value - currentValue)).coerceIn(
                        0f,
                        1f
                    )) * (mSelectedTextSize - mTextSize) + mTextSize,
                    resources.displayMetrics
                )
        }

        val ratio = (1 - (abs(value - currentValue)).coerceIn(0f, 1f))

        canvas.drawText(
            displayValues?.getOrNull((value + minValue) % (maxValue - minValue))
                ?: ((value + minValue) % (maxValue - minValue)).toString(),
            (indicatorIntervalWidth * value).toFloat() + indicatorIntervalWidth / 2,
            mViewHeight - ((mSelectedIndicatorHeight - mIndicatorHeight) * ratio + mIndicatorHeight) - mTextMarginBottom - mSelectedTextPaint!!.textSize / 2,
            mTextPaint!!
        )
    }
    /////////////////////// Properties getter/setter ///////////////////////
    /**
     * @return Color integer value of the ruler text color.
     * @see .setTextColor
     */
    /**
     * Set the color of the text to display on the ruler.
     *
     * @param color Color integer value.
     */
    @get:ColorInt
    @get:CheckResult
    var textColor: Int
        get() = mIndicatorColor
        set(color) {
            mTextColor = color
            refreshPaint()
        }

    /**
     * @return Size of the text of ruler in pixels.
     * @see .setTextSize
     */
    @get:CheckResult
    val textSize: Float
        get() = mTextSize.toFloat()

    /**
     * Set the size of the text to display on the ruler.
     *
     * @param textSizeSp Text size dimension in com.example.salehub.utils.getDp.
     */
    fun setTextSize(textSizeSp: Int) {
        mTextSize = RulerViewUtils.sp2px(context, textSizeSp.toFloat())
        refreshPaint()
    }
    /**
     * @return Color integer value of the indicator color.
     * @see .setIndicatorColor
     */
    /**
     * Set the indicator color.
     *
     * @param color Color integer value.
     */
    @get:ColorInt
    @get:CheckResult
    var indicatorColor: Int
        get() = mIndicatorColor
        set(color) {
            mIndicatorColor = color
            refreshPaint()
        }

    /**
     * Set the width of the indicator line in the ruler.
     *
     * @param widthPx Width in pixels.
     */
    fun setIndicatorMaxWidth(widthPx: Int) {
        indicatorMaxWidth = widthPx.toFloat()
        refreshPaint()
    }

    fun setIndicatorMinWidth(widthPx: Int) {
        indicatorMinWidth = widthPx.toFloat()
        refreshPaint()
    }

    /**
     * Set the maximum value to display on the ruler. This will decide the range of values and number
     * of indicators that ruler will draw.
     *
     * @param minValue Value to display at the left end of the ruler. This can be positive, negative
     * or zero. Default minimum value is 0.
     * @param maxValue Value to display at the right end of the ruler. This can be positive, negative
     * or zero.This value must be greater than min value. Default minimum value is 100.
     */
    fun setValueRange(minValue: Int, maxValue: Int) {
        this.minValue = minValue
        this.maxValue = maxValue
        invalidate()
    }

    private var displayValues: List<String>? = null

    fun setDisplayValues(valueList: List<String>) {
        displayValues = valueList
    }

    /**
     * Set the spacing between two vertical lines/indicators. Default value is 14 pixels.
     *
     * @param indicatorIntervalPx Distance in pixels. This cannot be negative number or zero.
     * @throws IllegalArgumentException if interval is negative or zero.
     */
    fun setIndicatorIntervalDistance(indicatorIntervalPx: Int) {
        require(indicatorIntervalPx > 0) { "Interval cannot be negative or zero." }
        indicatorIntervalWidth = indicatorIntervalPx
        invalidate()
    }

//    /**
//     * Set the height of the long and short indicators.
//     *
//     * @param longHeightRatio  Ratio of long indicator height to the ruler height. This value must
//     * be between 0 to 1. The value should greater than [.mShortIndicatorHeight].
//     * Default value is 0.6 (i.e. 60%). If the value is 0, indicator won't
//     * be displayed. If the value is 1, indicator height will be same as the
//     * ruler height.
//     * @param shortHeightRatio Ratio of short indicator height to the ruler height. This value must
//     * be between 0 to 1. The value should less than [.mLongIndicatorHeight].
//     * Default value is 0.4 (i.e. 40%). If the value is 0, indicator won't
//     * be displayed. If the value is 1, indicator height will be same as
//     * the ruler height.
//     * @throws IllegalArgumentException if any of the parameter is invalid.
//     */
//    fun setIndicatorHeight(
//        longHeightRatio: Float,
//        shortHeightRatio: Float
//    ) {
//        require(!(shortHeightRatio < 0 || shortHeightRatio > 1)) { "Sort indicator height must be between 0 to 1." }
//        require(!(longHeightRatio < 0 || longHeightRatio > 1)) { "Long indicator height must be between 0 to 1." }
//        require(shortHeightRatio <= longHeightRatio) { "Long indicator height cannot be less than sort indicator height." }
//        longIndicatorHeightRatio = longHeightRatio
//        shortIndicatorHeightRatio = shortHeightRatio
//        updateIndicatorHeight(longIndicatorHeightRatio, shortIndicatorHeightRatio)
//        invalidate()
//    }
}