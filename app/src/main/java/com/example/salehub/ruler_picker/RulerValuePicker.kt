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
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.CheckResult
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import com.example.salehub.R
import com.example.salehub.ruler_picker.RulerView.Companion.MULTIPLIER
import com.kevalpatel2106.rulerpicker.RulerViewUtils
import kotlin.math.roundToInt

/**
 * Created by Kevalpatel2106 on 29-Mar-2018.
 *
 *
 *  * Diagram:
 * Observable ScrollView
 * |------------------|---------------------\--/----------------------|------------------|<br></br>
 * |                  |                      \/                       |                  |<br></br>
 * |                  |                                               |                  |<br></br>
 * |  Left Spacer     |                 RulerView                     |  Right Spacer    |<br></br>
 * |                  |                                               |                  |<br></br>
 * |                  |                                               |                  |<br></br>
 * |------------------|-----------------------------------------------|------------------|<br></br>
 *
 * @see [](https://github.com/dwfox/DWRulerView>Original Repo</a>
) */
class RulerValuePicker : FrameLayout, ObservableHorizontalScrollView.ScrollChangedListener {
    /**
     * Left side empty view to add padding to the ruler.
     */
    @NonNull
    private var mLeftSpacer: View? = null

    /**
     * Right side empty view to add padding to the ruler.
     */
    @NonNull
    private var mRightSpacer: View? = null

    /**
     * Ruler view with values.
     */
    @NonNull
    private var mRulerView: RulerView? = null

    /**
     * [ObservableHorizontalScrollView], that will host all three components.
     *
     * @see .mLeftSpacer
     *
     * @see .mRightSpacer
     *
     * @see .mRulerView
     */
    @NonNull
    private var mHorizontalScrollView: ObservableHorizontalScrollView? = null

    @Nullable
    private var mListener: RulerValuePickerListener? = null

    @NonNull
    private var mNotchPaint: Paint? = null

    @NonNull
    private var mNotchPath: Path? = null
    private var mNotchColor = Color.WHITE

    /**
     * Public constructor.
     */
    constructor(@NonNull context: Context?) : super(context!!) {
        init(null)
    }

    /**
     * Public constructor.
     */
    constructor(
        @NonNull context: Context?,
        @Nullable attrs: AttributeSet?
    ) : super(context!!, attrs) {
        init(attrs)
    }

    /**
     * Public constructor.
     */
    constructor(
        @NonNull context: Context?,
        @Nullable attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context!!, attrs, defStyleAttr) {
        init(attrs)
    }

    /**
     * Public constructor.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
        @NonNull context: Context?,
        @Nullable attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context!!, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    /**
     * Initialize the view and parse the [AttributeSet].
     *
     * @param attributeSet [AttributeSet] to parse or null if no attribute parameters set.
     */
    private fun init(@Nullable attributeSet: AttributeSet?) {

        //Add all the children
        addChildViews()
        if (attributeSet != null) {
            val a = context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.RulerValuePicker,
                0,
                0
            )
            try { //Parse params
                if (a.hasValue(R.styleable.RulerValuePicker_notch_color)) {
                    mNotchColor = a.getColor(R.styleable.RulerValuePicker_notch_color, Color.WHITE)
                }
                if (a.hasValue(R.styleable.RulerValuePicker_ruler_text_color)) {
                    textColor = a.getColor(
                        R.styleable.RulerValuePicker_ruler_text_color,
                        Color.WHITE
                    )
                }
                if (a.hasValue(R.styleable.RulerValuePicker_ruler_text_size)) {
                    setTextSize(
                        a.getDimension(R.styleable.RulerValuePicker_ruler_text_size, 14f).toInt()
                    )
                }
                if (a.hasValue(R.styleable.RulerValuePicker_indicator_color)) {
                    indicatorColor = a.getColor(
                        R.styleable.RulerValuePicker_indicator_color,
                        Color.WHITE
                    )
                }
                setIndicatorMaxWidth(
                    a.getDimensionPixelSize(
                        R.styleable.RulerValuePicker_indicator_max_width,
                        RulerView.DEFAULT_INDICATOR_MAX_WIDTH.toInt()
                    )
                )
                setIndicatorMaxWidth(
                    a.getDimensionPixelSize(
                        R.styleable.RulerValuePicker_indicator_min_width,
                        RulerView.DEFAULT_INDICATOR_MIN_WIDTH.toInt()
                    )
                )
                if (a.hasValue(R.styleable.RulerValuePicker_indicator_interval)) {
                    setIndicatorIntervalDistance(
                        a.getDimensionPixelSize(
                            R.styleable.RulerValuePicker_indicator_interval,
                            4
                        )
                    )
                }
                setSelectedIndicatorHeight(a.getDimensionPixelSize(
                    R.styleable.RulerValuePicker_selected_indicator_height,
                    RulerView.DEFAULT_SELECTED_INDICATOR_HEIGHT
                ))
                setIndicatorHeight(a.getDimensionPixelSize(
                    R.styleable.RulerValuePicker_indicator_height,
                    RulerView.DEFAULT_INDICATOR_HEIGHT
                ))
//                if (a.hasValue(R.styleable.RulerValuePicker_long_height_height_ratio)
//                    || a.hasValue(R.styleable.RulerValuePicker_short_height_height_ratio)
//                ) {
//                    setIndicatorHeight(
//                        a.getFraction(
//                            R.styleable.RulerValuePicker_long_height_height_ratio,
//                            1, 1, 0.6f
//                        ),
//                        a.getFraction(
//                            R.styleable.RulerValuePicker_short_height_height_ratio,
//                            1, 1, 0.4f
//                        )
//                    )
//                }
                if (a.hasValue(R.styleable.RulerValuePicker_min_value) ||
                    a.hasValue(R.styleable.RulerValuePicker_max_value)
                ) {
                    setMinMaxValue(
                        a.getInteger(R.styleable.RulerValuePicker_min_value, 0),
                        a.getInteger(R.styleable.RulerValuePicker_max_value, 100)
                    )
                }
            } finally {
                a.recycle()
            }
        }

        //Prepare the notch color.
        mNotchPaint = Paint()
        prepareNotchPaint()
        mNotchPath = Path()
    }

    /**
     * Create the paint for notch. This will
     */
    private fun prepareNotchPaint() {
        mNotchPaint!!.color = mNotchColor
        mNotchPaint!!.strokeWidth = 5f
        mNotchPaint!!.style = Paint.Style.FILL_AND_STROKE
    }

    /**
     * Programmatically add the children to the view.
     *
     *
     *  * The main view contains the [android.widget.HorizontalScrollView]. That allows
     * [RulerView] to scroll horizontally.
     *  * [.mHorizontalScrollView] contains [LinearLayout] that will act as the container
     * to hold the children inside the horizontal view.
     *  * [LinearLayout] container will contain three children.
     * **Left spacer:** Width of this view will be the half width of the view. This will add staring at the start of the ruler.
     * **Right spacer:** Width of this view will be the half width of the view. This will add ending at the end of the ruler.
     * **[RulerView]:** Ruler view will contain the ruler with indicator.
     *
     */
    private fun addChildViews() {
        mHorizontalScrollView = ObservableHorizontalScrollView(context, this)
        mHorizontalScrollView?.isHorizontalScrollBarEnabled = false //Don't display the scrollbar
        val rulerContainer = LinearLayout(context)

        //Add left spacing to the container
        mLeftSpacer = View(context)
        rulerContainer.addView(mLeftSpacer)

        //Add ruler to the container
        mRulerView = RulerView(context)
        rulerContainer.addView(mRulerView)

        //Add right spacing to the container
        mRightSpacer = View(context)
        rulerContainer.addView(mRightSpacer)

        //Add this container to the scroll view.
        mHorizontalScrollView?.removeAllViews()
        mHorizontalScrollView?.addView(rulerContainer)

        //Add scroll view to this view.
        removeAllViews()
        addView(mHorizontalScrollView)
    }

    var layoutParamsHolder: ViewGroup.LayoutParams? = null

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val desireHeight = RulerViewUtils.sp2px(
            context,
            mRulerView!!.mSelectedTextSize.toFloat()
        ) * 1.5 + mRulerView!!.mTextMarginBottom + mRulerView!!.mSelectedIndicatorHeight + mRulerView!!.mIndicatorRadius
        setMeasuredDimension(width, desireHeight.toInt())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //Draw the top notch
//        canvas.drawPath(mNotchPath!!, mNotchPaint!!)
    }

    override fun onLayout(isChanged: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(isChanged, left, top, right, bottom)
        if (isChanged) {
            val width = width

            //Set width of the left spacer to the half of this view.
            val leftParams = mLeftSpacer!!.layoutParams
            leftParams.width = width / 2 - width / 10
            mLeftSpacer!!.layoutParams = leftParams

            //Set width of the right spacer to the half of this view.
            val rightParams = mRightSpacer!!.layoutParams
            rightParams.width = width / 2 - width / 10
            mRightSpacer!!.layoutParams = rightParams
            calculateNotchPath()
            invalidate()
        }
    }

    /**
     * Calculate notch path. Notch will be in the triangle shape at the top-center of this view.
     *
     * @see .mNotchPath
     */
    private fun calculateNotchPath() {
        mNotchPath!!.reset()
        mNotchPath!!.moveTo((width / 2 - 30).toFloat(), 0f)
        mNotchPath!!.lineTo((width / 2).toFloat(), 40f)
        mNotchPath!!.lineTo((width / 2 + 30).toFloat(), 0f)
    }

    /**
     * Scroll the ruler to the given value.
     *
     * @param value Value to select. Value must be between [.getMinValue] and [.getMaxValue].
     * If the value is less than [.getMinValue], [.getMinValue] will be
     * selected.If the value is greater than [.getMaxValue], [.getMaxValue]
     * will be selected.
     */
    fun selectValue(value: Int) {
        mHorizontalScrollView?.postDelayed({
            val valuesToScroll: Int
            if (value < mRulerView?.minValue ?: 0) {
                valuesToScroll = 0
            } else if (value > mRulerView?.maxValue ?: 0) {
                valuesToScroll = mRulerView?.maxValue ?: 0 - mRulerView!!.minValue ?: 0
            } else {
                valuesToScroll = value - mRulerView!!.minValue
            }
            mHorizontalScrollView?.scrollTo(
                (valuesToScroll + MULTIPLIER / 2 - 2) * mRulerView!!.indicatorIntervalWidth, 0
            )
        }, 400)
    }

    /**
     * @return Get the current selected value.
     */
    val selectedValue: Int
        get() {
            val absoluteValue =
                (mHorizontalScrollView!!.scrollX.toFloat() / mRulerView!!.indicatorIntervalWidth).roundToInt()
            val value: Int = mRulerView!!.minValue + absoluteValue
            return if (value > mRulerView!!.maxValue * MULTIPLIER) {
                mRulerView!!.maxValue * MULTIPLIER
            } else if (value < mRulerView!!.minValue * MULTIPLIER) {
                mRulerView!!.minValue * MULTIPLIER
            } else {
                value
            }
        }

    val currentValue: Float
        get() {
            val absoluteValue =
                mHorizontalScrollView!!.scrollX.toFloat() / mRulerView!!.indicatorIntervalWidth
            val value: Float = mRulerView!!.minValue + absoluteValue
//            Log.e("TAG", "value : ${value}", )
//            Log.e("TAG", "aaa: ${mRulerView!!.maxValue * MULTIPLIER}", )
//            Log.e("TAG", "bbb: ${value > mRulerView!!.maxValue * MULTIPLIER}", )

            return if (value > mRulerView!!.maxValue * MULTIPLIER) {
                mRulerView!!.maxValue.toFloat() * MULTIPLIER
            } else if (value < mRulerView!!.minValue * MULTIPLIER) {
                mRulerView!!.minValue.toFloat() * MULTIPLIER
            } else {
                value
            }
        }

    override fun onScrollChanged() {
        mRulerView?.currentValue = currentValue
        if (mListener != null) mListener!!.onIntermediateValueChange(currentValue)
    }

    override fun onScrollStopped() {
        makeOffsetCorrection(mRulerView!!.indicatorIntervalWidth)
        mRulerView?.selectedValue = selectedValue
        if (mListener != null) {
            mListener!!.onValueChange(selectedValue % (maxValue - minValue))
        }
    }

    private fun makeOffsetCorrection(indicatorInterval: Int) {
        val offsetValue: Int = mHorizontalScrollView!!.scrollX % indicatorInterval
        if (offsetValue < indicatorInterval / 2) {
            mHorizontalScrollView!!.smoothScrollBy(-offsetValue, 0)
        } else {
            mHorizontalScrollView!!.smoothScrollBy(indicatorInterval - offsetValue, 0)
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.value = selectedValue
        return ss
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        selectValue(ss.value)
    }
    //**********************************************************************************//
    //******************************** GETTERS/SETTERS *********************************//
    //**********************************************************************************//
    /**
     * @param notchColorRes Color resource of the notch to display. Default color os [Color.WHITE].
     * @see .setNotchColor
     * @see .getNotchColor
     */
    fun setNotchColorRes(@ColorRes notchColorRes: Int) {
        notchColor = ContextCompat.getColor(context, notchColorRes)
    }
    /**
     * @return Integer color of the notch. Default color os [Color.WHITE].
     * @see .setNotchColor
     * @see .setNotchColorRes
     */
    /**
     * @param notchColor Integer color of the notch to display. Default color os [Color.WHITE].
     * @see .prepareNotchPaint
     * @see .getNotchColor
     */
    @get:ColorInt
    var notchColor: Int
        get() = mNotchColor
        set(notchColor) {
            mNotchColor = notchColor
            prepareNotchPaint()
            invalidate()
        }
    /**
     * @return Color integer value of the ruler text color.
     * @see .setTextColor
     * @see .setTextColorRes
     */
    /**
     * Set the color of the text to display on the ruler.
     *
     * @param color Color integer value.
     * @see .getTextColor
     * @see RulerView.mTextColor
     */
    @get:ColorInt
    @get:CheckResult
    var textColor: Int
        get() = mRulerView!!.textColor
        set(color) {
            mRulerView?.textColor = color
        }

    /**
     * Set the color of the text to display on the ruler.
     *
     * @param color Color resource id.
     * @see RulerView.mTextColor
     */
    fun setTextColorRes(@ColorRes color: Int) {
        textColor = ContextCompat.getColor(context, color)
    }

    /**
     * @return Size of the text of ruler in com.example.salehub.utils.getDp.
     * @see .setTextSize
     * @see .setTextSizeRes
     * @see RulerView.mTextColor
     */
    @get:CheckResult
    val textSize: Float
        get() = mRulerView!!.textSize

    /**
     * Set the size of the text to display on the ruler.
     *
     * @param dimensionDp Text size dimension in com.example.salehub.utils.getDp.
     * @see .getTextSize
     * @see RulerView.mTextSize
     */
    fun setTextSize(dimensionDp: Int) {
        mRulerView?.setTextSize(dimensionDp)
    }

    /**
     * Set the size of the text to display on the ruler.
     *
     * @param dimension Text size dimension resource.
     * @see .getTextSize
     * @see RulerView.mTextSize
     */
    fun setTextSizeRes(@DimenRes dimension: Int) {
        setTextSize(context.resources.getDimension(dimension).toInt())
    }
    /**
     * @return Color integer value of the indicator color.
     * @see .setIndicatorColor
     * @see .setIndicatorColorRes
     * @see RulerView.mIndicatorColor
     */
    /**
     * Set the indicator color.
     *
     * @param color Color integer value.
     * @see .getIndicatorColor
     * @see RulerView.mIndicatorColor
     */
    @get:ColorInt
    @get:CheckResult
    var indicatorColor: Int
        get() = mRulerView!!.indicatorColor
        set(color) {
            mRulerView?.indicatorColor = color
        }

    /**
     * Set the indicator color.
     *
     * @param color Color resource id.
     * @see .getIndicatorColor
     * @see RulerView.mIndicatorColor
     */
    fun setIndicatorColorRes(@ColorRes color: Int) {
        indicatorColor = ContextCompat.getColor(context, color)
    }

    /**
     * @return Width of the indicator in pixels.
     * @see .setIndicatorWidth
     * @see .setIndicatorWidthRes
     * @see RulerView.mIndicatorWidthPx
     */
    @get:CheckResult
    val indicatorWidth: Float
        get() = mRulerView!!.indicatorMaxWidth

    /**
     * Set the width of the indicator line in the ruler.
     *
     * @param widthPx Width in pixels.
     * @see .getIndicatorWidth
     * @see RulerView.mIndicatorWidthPx
     */
    fun setIndicatorMaxWidth(widthPx: Int) {
        mRulerView?.setIndicatorMaxWidth(widthPx)
    }

    fun setIndicatorMinWidth(widthPx: Int) {
        mRulerView?.setIndicatorMinWidth(widthPx)
    }

    /**
     * Set the width of the indicator line in the ruler.
     *
     * @param width Dimension resource for indicator width.
     * @see .getIndicatorWidth
     * @see RulerView.mIndicatorWidthPx
     */
//    fun setIndicatorWidthRes(@DimenRes width: Int) {
//        setIndicatorWidth(context.resources.getDimensionPixelSize(width))
//    }

    /**
     * @return Get the minimum value displayed on the ruler.
     * @see .setMinMaxValue
     * @see RulerView.mMinValue
     */
    @get:CheckResult
    val minValue: Int
        get() = mRulerView!!.minValue

    /**
     * @return Get the maximum value displayed on the ruler.
     * @see .setMinMaxValue
     * @see RulerView.mMaxValue
     */
    @get:CheckResult
    val maxValue: Int
        get() = mRulerView!!.maxValue

    /**
     * Set the maximum value to display on the ruler. This will decide the range of values and number
     * of indicators that ruler will draw.
     *
     * @param minValue Value to display at the left end of the ruler. This can be positive, negative
     * or zero. Default minimum value is 0.
     * @param maxValue Value to display at the right end of the ruler. This can be positive, negative
     * or zero.This value must be greater than min value. Default minimum value is 100.
     * @see .getMinValue
     * @see .getMaxValue
     */
    fun setMinMaxValue(minValue: Int, maxValue: Int) {
        mRulerView?.setValueRange(minValue, maxValue)
        invalidate()
        selectValue(minValue)
    }

    fun setDisplayValues(valueList: List<String>) {
        mRulerView?.setDisplayValues(valueList)
        mRulerView?.setValueRange(0, valueList.size)
        invalidate()
        selectValue(0)
    }

    /**
     * @return Get distance between two indicator in pixels.
     * @see .setIndicatorIntervalDistance
     * @see RulerView.mIndicatorInterval
     */
    @get:CheckResult
    val indicatorIntervalWidth: Int
        get() = mRulerView!!.indicatorIntervalWidth

    /**
     * Set the spacing between two vertical lines/indicators. Default value is 14 pixels.
     *
     * @param indicatorIntervalPx Distance in pixels. This cannot be negative number or zero.
     * @see RulerView.mIndicatorInterval
     */
    fun setIndicatorIntervalDistance(indicatorIntervalPx: Int) {
        mRulerView?.setIndicatorIntervalDistance(indicatorIntervalPx)
    }

    fun setSelectedIndicatorHeight(heightPx: Int) {
        mRulerView?.mSelectedIndicatorHeight = heightPx
    }

    fun setIndicatorHeight(heightPx: Int) {
        mRulerView?.mIndicatorHeight = heightPx
    }

//    /**
//     * @return Ratio of long indicator height to the ruler height.
//     * @see .setIndicatorHeight
//     * @see RulerView.mLongIndicatorHeightRatio
//     */
//    @get:CheckResult
//    val longIndicatorHeightRatio: Float
//        get() = mRulerView!!.longIndicatorHeightRatio
//
//    /**
//     * @return Ratio of short indicator height to the ruler height.
//     * @see .setIndicatorHeight
//     * @see RulerView.mShortIndicatorHeight
//     */
//    @get:CheckResult
//    val shortIndicatorHeightRatio: Float
//        get() = mRulerView!!.shortIndicatorHeightRatio

//    /**
//     * Set the height of the long and short indicators.
//     *
//     * @param longHeightRatio  Ratio of long indicator height to the ruler height. This value must
//     * be between 0 to 1. The value should greater than [.getShortIndicatorHeightRatio].
//     * Default value is 0.6 (i.e. 60%). If the value is 0, indicator won't
//     * be displayed. If the value is 1, indicator height will be same as the
//     * ruler height.
//     * @param shortHeightRatio Ratio of short indicator height to the ruler height. This value must
//     * be between 0 to 1. The value should less than [.getLongIndicatorHeightRatio].
//     * Default value is 0.4 (i.e. 40%). If the value is 0, indicator won't
//     * be displayed. If the value is 1, indicator height will be same as
//     * the ruler height.
//     * @see .getLongIndicatorHeightRatio
//     * @see .getShortIndicatorHeightRatio
//     */
//    fun setIndicatorHeight(
//        longHeightRatio: Float,
//        shortHeightRatio: Float
//    ) {
//        mRulerView?.setIndicatorHeight(longHeightRatio, shortHeightRatio)
//    }

    /**
     * Set the [RulerValuePickerListener] to get callbacks when the value changes.
     *
     * @param listener [RulerValuePickerListener]
     */
    fun setValuePickerListener(@Nullable listener: RulerValuePickerListener?) {
        mListener = listener
    }

    /**
     * User interface state that is stored by RulerView for implementing
     * [View.onSaveInstanceState].
     */
    class SavedState : BaseSavedState {
        var value = 0

        internal constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            value = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(value)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<SavedState> {
            override fun createFromParcel(parcel: Parcel): SavedState {
                return SavedState(parcel)
            }

            override fun newArray(size: Int): Array<SavedState?> {
                return arrayOfNulls(size)
            }
        }
    }
}