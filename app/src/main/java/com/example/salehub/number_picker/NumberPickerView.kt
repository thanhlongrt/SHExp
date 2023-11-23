package com.example.salehub.number_picker

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.text.TextPaint
import android.text.TextUtils
import android.text.TextUtils.TruncateAt
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.Scroller
import com.example.salehub.R
import java.util.concurrent.ConcurrentHashMap

@Suppress("LargeClass", "ComplexMethod")
class NumberPickerView : View {
    private var mTextColorNormal = DEFAULT_TEXT_COLOR_NORMAL
    private var mTextColorSelected = DEFAULT_TEXT_COLOR_SELECTED
    private var mTextColorHint = DEFAULT_TEXT_COLOR_SELECTED
    private var mTextSizeNormal = 0
    private var mTextSizeSelected = 0
    private var mTextSizeHint = 0
    private var mWidthOfHintText = 0
    private var mWidthOfAlterHint = 0
    private var mMarginStartOfHint = 0
    private var mMarginEndOfHint = 0
    private var mItemPaddingVertical = 0
    private var mItemPaddingHorizontal = 0
    private var mDividerColor = DEFAULT_DIVIDER_COLOR
    private var mDividerHeight = DEFAULT_DIVIDER_HEIGHT
    private var mDividerMarginL = DEFAULT_DIVIDER_MARGIN_HORIZONTAL
    private var mDividerMarginR = DEFAULT_DIVIDER_MARGIN_HORIZONTAL
    private var mShownCount = DEFAULT_SHOWN_COUNT
    private var mDividerIndex0 = 0
    private var mDividerIndex1 = 0
    private var mMinShowIndex = -1
    private var mMaxShowIndex = -1

    // compat for android.widget.NumberPicker
    private var mMinValue = 0

    // compat for android.widget.NumberPicker
    private var mMaxValue = 0
    private var mMaxWidthOfDisplayedValues = 0
    private var mMaxHeightOfDisplayedValues = 0
    private var mMaxWidthOfAlterArrayWithMeasureHint = 0
    private var mMaxWidthOfAlterArrayWithoutMeasureHint = 0
    private var mPrevPickedIndex = 0
    private var mMiniVelocityFling = 100
    private var mScaledTouchSlop = 8
    private var mHintText: String? = null
    private var mTextEllipsize: String? = null
    private var mEmptyItemHint: String? = null
    private var mAlterHint: String? = null

    // friction used by scroller when fling
    private var mFriction = 1f
    private var mTextSizeNormalCenterYOffset = 0f
    private var mTextSizeSelectedCenterYOffset = 0f
    private var mTextSizeHintCenterYOffset = 0f

    private var mShowDivider = DEFAULT_SHOW_DIVIDER

    private var mWrapSelectorWheel = DEFAULT_WRAP_SELECTOR_WHEEL

    private var mCurrentItemIndexEffect = DEFAULT_CURRENT_ITEM_INDEX_EFFECT

    private var mHasInit = false

    private var mWrapSelectorWheelCheck = true

    private var mPendingWrapToLinear = false
    private var mRespondChangeOnDetach = DEFAULT_RESPOND_CHANGE_ON_DETACH

    private var mRespondChangeInMainThread = DEFAULT_RESPOND_CHANGE_IN_MAIN_THREAD

    private var mSelectedItemBackground: Drawable? = null
    private var mScroller: Scroller? = null
    private var mVelocityTracker: VelocityTracker? = null
    private val mPaintDivider = Paint()
    private val mPaintText: TextPaint = TextPaint()
    private val mPaintHint: Paint = Paint()
    private var mDisplayedValues: Array<String?>? = null
    private var mAlterTextArrayWithMeasureHint: Array<CharSequence?>? = null
    private var mAlterTextArrayWithoutMeasureHint: Array<CharSequence?>? = null
    private var mHandlerThread: HandlerThread? = null
    private var mHandlerInNewThread: Handler? = null
    private var mHandlerInMainThread: Handler? = null
    private val mTextWidthCache: MutableMap<String, Int> = ConcurrentHashMap()

    private var mOnValueChangeListenerRaw: OnValueChangeListenerRelativeToRaw? = null
    private var mOnValueChangeListener: OnValueChangeListener? = // compatible for NumberPicker
        null
    private var mOnScrollListener: OnScrollListener? = // compatible for NumberPicker
        null
    private var mOnValueChangeListenerInScrolling: OnValueChangeListenerInScrolling? = // response onValueChanged in scrolling
        null
    private var mFlagMayPress = false

    /**
     * Gets the values to be displayed instead of string values.
     *
     * @return The displayed values.
     */
    var displayedValues: Array<String?>?
        get() = mDisplayedValues
        set(newDisplayedValues) {
            stopRefreshing()
            stopScrolling()
            if (newDisplayedValues == null) {
                throw IllegalArgumentException("newDisplayedValues should not be null.")
            }
            if (mMaxValue - mMinValue + 1 > newDisplayedValues.size) {
                throw IllegalArgumentException(
                    "mMaxValue - mMinValue + 1 should not be greater than mDisplayedValues.length, now " +
                        "((mMaxValue - mMinValue + 1) is " + (mMaxValue - mMinValue + 1) +
                        " newDisplayedValues.length is " + newDisplayedValues.size +
                        ", you need to set MaxValue and MinValue before setDisplayedValues(String[])"
                )
            }
            updateContent(newDisplayedValues)
            updateMaxWHOfDisplayedValues(true)
            mPrevPickedIndex = 0 + mMinShowIndex
            correctPositionByDefaultValue(0, mWrapSelectorWheel && mWrapSelectorWheelCheck)
            postInvalidate()
            mHandlerInMainThread?.sendEmptyMessage(HANDLER_WHAT_REQUEST_LAYOUT)
        }

    // The current scroll state of the NumberPickerView.
    private var mScrollState = OnScrollListener.SCROLL_STATE_IDLE

    private var mInScrollingPickedOldValue = 0
    private var mInScrollingPickedNewValue = 0

    var minValue: Int
        get() = mMinValue
        set(minValue) {
            mMinValue = minValue
            mMinShowIndex = 0
            updateNotWrapYLimit()
        }

    var maxValue: Int
        get() = mMaxValue
        set(maxValue) {
            if (mDisplayedValues == null) {
                throw NullPointerException("mDisplayedValues should not be null")
            }
            if (maxValue - mMinValue + 1 > mDisplayedValues?.size ?: 0) {
                throw IllegalArgumentException(
                    (
                        "(maxValue - mMinValue + 1) should not be greater than mDisplayedValues.length now " +
                            " (maxValue - mMinValue + 1) is " +
                            (maxValue - mMinValue + 1) +
                            " and mDisplayedValues.length is " +
                            mDisplayedValues?.size
                        )
                )
            }
            mMaxValue = maxValue
            mMaxShowIndex = mMaxValue - mMinValue + mMinShowIndex
            setMinAndMaxShowIndex(mMinShowIndex, mMaxShowIndex)
            updateNotWrapYLimit()
        }

    var value: Int
        get() = pickedIndexRelativeToRaw + mMinValue
        set(value) {
            pickedIndexRelativeToRaw = value - mMinValue
        }

    val contentByCurrValue: String?
        get() = mDisplayedValues?.get(value - mMinValue)

    var pickedIndexRelativeToRaw: Int
        get() {
            val willPickIndex: Int = if (mCurrDrawFirstItemY != 0) {
                if (mCurrDrawFirstItemY < (-mItemHeight / 2)) {
                    getWillPickIndexByGlobalY(mCurrDrawGlobalY + mItemHeight + mCurrDrawFirstItemY)
                } else {
                    getWillPickIndexByGlobalY(mCurrDrawGlobalY + mCurrDrawFirstItemY)
                }
            } else {
                getWillPickIndexByGlobalY(mCurrDrawGlobalY)
            }
            return willPickIndex
        }
        set(pickedIndexToRaw) {
            if (mMinShowIndex > -1 && pickedIndexToRaw in mMinShowIndex..mMaxShowIndex) {
                mPrevPickedIndex = pickedIndexToRaw
                correctPositionByDefaultValue(
                    pickedIndexToRaw - mMinShowIndex,
                    mWrapSelectorWheel && mWrapSelectorWheelCheck
                )
                postInvalidate()
            }
        }

    private var mNotWrapLimitYTop = 0
    private var mNotWrapLimitYBottom = 0

    // first shown item's content index, corresponding to the Index of mDisplayedValued
    private var mCurrDrawFirstItemIndex = 0

    // the first shown item's Y
    private var mCurrDrawFirstItemY = 0

    // global Y corresponding to scroller
    private var mCurrDrawGlobalY = 0

    private val oneRecycleSize: Int
        get() = mMaxShowIndex - mMinShowIndex + 1

    private var downYGlobal = 0f
    private var downY = 0f
    private var currY = 0f

    private val ellipsizeType: TruncateAt
        private get() {
            when (mTextEllipsize) {
                TEXT_ELLIPSIZE_START -> return TruncateAt.START
                TEXT_ELLIPSIZE_MIDDLE -> return TruncateAt.MIDDLE
                TEXT_ELLIPSIZE_END -> return TruncateAt.END
                else -> throw IllegalArgumentException("Illegal text ellipsize type.")
            }
        }

    private var mSpecModeW = MeasureSpec.UNSPECIFIED
    private var mSpecModeH = MeasureSpec.UNSPECIFIED

    private var mViewWidth = 0
    private var mViewHeight = 0
    private var mItemHeight = 0
    private var dividerY0 = 0f
    private var dividerY1 = 0f
    private var mViewCenterX = 0f

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initAttr(context, attrs)
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initAttr(context, attrs)
        init(context)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        updateMaxWHOfDisplayedValues(false)
        setMeasuredDimension(
            measureWidth(widthMeasureSpec),
            measureHeight(heightMeasureSpec)
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mViewWidth = w
        mViewHeight = h
        mItemHeight = mViewHeight / mShownCount
        mViewCenterX = (mViewWidth + paddingLeft - paddingRight).toFloat() / 2
        var defaultValue = 0
        if (oneRecycleSize > 1) {
            defaultValue = if (mHasInit) {
                value - mMinValue
            } else if (mCurrentItemIndexEffect) {
                mCurrDrawFirstItemIndex + (mShownCount - 1) / 2
            } else {
                0
            }
        }
        correctPositionByDefaultValue(defaultValue, mWrapSelectorWheel && mWrapSelectorWheelCheck)
        updateFontAttr()
        updateNotWrapYLimit()
        updateDividerAttr()
        mHasInit = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mHandlerThread == null || mHandlerThread?.isAlive == false) {
            initHandler()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mHandlerThread?.quit()
        // These codes are for dialog or PopupWindow which will be used for more than once.
        // Not an elegant solution, if you have any good idea, please let me know, thank you.
        if (mItemHeight == 0) {
            return
        }
        if (mScroller?.isFinished == true) {
            mScroller?.abortAnimation()
            mCurrDrawGlobalY = mScroller?.currY ?: 0
            calculateFirstItemParameterByGlobalY()
            if (mCurrDrawFirstItemY != 0) {
                mCurrDrawGlobalY += if (mCurrDrawFirstItemY < -mItemHeight / 2) {
                    mItemHeight + mCurrDrawFirstItemY
                } else {
                    mCurrDrawFirstItemY
                }
                calculateFirstItemParameterByGlobalY()
            }
            onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE)
        }

        val currPickedIndex = getWillPickIndexByGlobalY(mCurrDrawGlobalY)
        if (currPickedIndex != mPrevPickedIndex && mRespondChangeOnDetach) {
            try {
                if (mOnValueChangeListener != null) {
                    mOnValueChangeListener?.onValueChange(
                        this@NumberPickerView,
                        mPrevPickedIndex + mMinValue,
                        currPickedIndex + mMinValue
                    )
                }
                if (mOnValueChangeListenerRaw != null) {
                    mOnValueChangeListenerRaw?.onValueChangeRelativeToRaw(
                        this@NumberPickerView,
                        mPrevPickedIndex,
                        currPickedIndex,
                        mDisplayedValues
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mPrevPickedIndex = currPickedIndex
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawSelectedItemBackground(canvas)
        drawContent(canvas)
        drawLine(canvas)
        drawHint(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        if (mItemHeight == 0) {
            return true
        }
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        mVelocityTracker?.addMovement(event)
        currY = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mFlagMayPress = true
                mHandlerInNewThread?.removeMessages(HANDLER_WHAT_REFRESH)
                stopScrolling()
                downY = currY
                downYGlobal = mCurrDrawGlobalY.toFloat()
                onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE)
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val spanY = downY - currY
                if (mFlagMayPress && (-mScaledTouchSlop < spanY && spanY < mScaledTouchSlop)) {
                } else {
                    mFlagMayPress = false
                    mCurrDrawGlobalY = limitY((downYGlobal + spanY).toInt())
                    calculateFirstItemParameterByGlobalY()
                    invalidate()
                }
                onScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                mHandlerInNewThread?.sendMessageDelayed(getMsg(HANDLER_WHAT_REFRESH), 0)
            }
            MotionEvent.ACTION_UP -> {
                click(event)
                val velocityTracker = mVelocityTracker
                velocityTracker?.computeCurrentVelocity(1000)
                val velocityY = (velocityTracker?.yVelocity ?: 0f * mFriction).toInt()
                if (Math.abs(velocityY) > mMiniVelocityFling) {
                    mScroller?.fling(
                        0,
                        mCurrDrawGlobalY,
                        0,
                        -velocityY,
                        Int.MIN_VALUE,
                        Int.MAX_VALUE,
                        limitY(Int.MIN_VALUE),
                        limitY(Int.MAX_VALUE)
                    )
                    invalidate()
                    onScrollStateChange(OnScrollListener.SCROLL_STATE_FLING)
                }
                mHandlerInNewThread?.sendMessageDelayed(getMsg(HANDLER_WHAT_REFRESH), 0)
                releaseVelocityTracker()
            }
            MotionEvent.ACTION_CANCEL -> {
                downYGlobal = mCurrDrawGlobalY.toFloat()
                stopScrolling()
                mHandlerInNewThread?.sendMessageDelayed(getMsg(HANDLER_WHAT_REFRESH), 0)
            }
        }
        return true
    }

    private fun respondPickedValueChangedInScrolling(oldVal: Int, newVal: Int) {
        mOnValueChangeListenerInScrolling?.onValueChangeInScrolling(this, oldVal, newVal)
    }

    private fun initAttr(context: Context, attrs: AttributeSet?) {
        if (attrs == null) {
            return
        }
        val a = context.obtainStyledAttributes(attrs, R.styleable.NumberPickerView)
        val n = a.indexCount
        for (i in 0 until n) {
            when (val attr = a.getIndex(i)) {
                R.styleable.NumberPickerView_npv_ShownCount -> {
                    mShownCount = a.getInt(attr, DEFAULT_SHOWN_COUNT)
                }
                R.styleable.NumberPickerView_npv_DividerColor -> {
                    mDividerColor = a.getColor(attr, DEFAULT_DIVIDER_COLOR)
                }
                R.styleable.NumberPickerView_npv_DividerHeight -> {
                    mDividerHeight = a.getDimensionPixelSize(attr, DEFAULT_DIVIDER_HEIGHT)
                }
                R.styleable.NumberPickerView_npv_DividerMarginLeft -> {
                    mDividerMarginL =
                        a.getDimensionPixelSize(attr, DEFAULT_DIVIDER_MARGIN_HORIZONTAL)
                }
                R.styleable.NumberPickerView_npv_DividerMarginRight -> {
                    mDividerMarginR =
                        a.getDimensionPixelSize(attr, DEFAULT_DIVIDER_MARGIN_HORIZONTAL)
                }
                R.styleable.NumberPickerView_npv_TextArray -> {
                    mDisplayedValues = convertCharSequenceArrayToStringArray(a.getTextArray(attr))
                }
                R.styleable.NumberPickerView_npv_TextColorNormal -> {
                    mTextColorNormal = a.getColor(attr, DEFAULT_TEXT_COLOR_NORMAL)
                }
                R.styleable.NumberPickerView_npv_TextColorSelected -> {
                    mTextColorSelected = a.getColor(attr, DEFAULT_TEXT_COLOR_SELECTED)
                }
                R.styleable.NumberPickerView_npv_TextColorHint -> {
                    mTextColorHint = a.getColor(attr, DEFAULT_TEXT_COLOR_SELECTED)
                }
                R.styleable.NumberPickerView_npv_TextSizeNormal -> {
                    mTextSizeNormal =
                        a.getDimensionPixelSize(
                            attr,
                            sp2px(context, DEFAULT_TEXT_SIZE_NORMAL_SP.toFloat())
                        )
                }
                R.styleable.NumberPickerView_npv_TextSizeSelected -> {
                    mTextSizeSelected =
                        a.getDimensionPixelSize(
                            attr,
                            sp2px(context, DEFAULT_TEXT_SIZE_SELECTED_SP.toFloat())
                        )
                }
                R.styleable.NumberPickerView_npv_TextSizeHint -> {
                    mTextSizeHint =
                        a.getDimensionPixelSize(
                            attr,
                            sp2px(context, DEFAULT_TEXT_SIZE_HINT_SP.toFloat())
                        )
                }
                R.styleable.NumberPickerView_npv_MinValue -> {
                    mMinShowIndex = a.getInteger(attr, 0)
                }
                R.styleable.NumberPickerView_npv_MaxValue -> {
                    mMaxShowIndex = a.getInteger(attr, 0)
                }
                R.styleable.NumberPickerView_npv_WrapSelectorWheel -> {
                    mWrapSelectorWheel = a.getBoolean(attr, DEFAULT_WRAP_SELECTOR_WHEEL)
                }
                R.styleable.NumberPickerView_npv_ShowDivider -> {
                    mShowDivider = a.getBoolean(attr, DEFAULT_SHOW_DIVIDER)
                }
                R.styleable.NumberPickerView_npv_HintText -> {
                    mHintText = a.getString(attr)
                }
                R.styleable.NumberPickerView_npv_AlternativeHint -> {
                    mAlterHint = a.getString(attr)
                }
                R.styleable.NumberPickerView_npv_EmptyItemHint -> {
                    mEmptyItemHint = a.getString(attr)
                }
                R.styleable.NumberPickerView_npv_MarginStartOfHint -> {
                    mMarginStartOfHint =
                        a.getDimensionPixelSize(
                            attr,
                            dp2px(context, DEFAULT_MARGIN_START_OF_HINT_DP.toFloat())
                        )
                }
                R.styleable.NumberPickerView_npv_MarginEndOfHint -> {
                    mMarginEndOfHint =
                        a.getDimensionPixelSize(
                            attr,
                            dp2px(context, DEFAULT_MARGIN_END_OF_HINT_DP.toFloat())
                        )
                }
                R.styleable.NumberPickerView_npv_ItemPaddingVertical -> {
                    mItemPaddingVertical =
                        a.getDimensionPixelSize(
                            attr,
                            dp2px(context, DEFAULT_ITEM_PADDING_DP_V.toFloat())
                        )
                }
                R.styleable.NumberPickerView_npv_ItemPaddingHorizontal -> {
                    mItemPaddingHorizontal =
                        a.getDimensionPixelSize(
                            attr,
                            dp2px(context, DEFAULT_ITEM_PADDING_DP_H.toFloat())
                        )
                }
                R.styleable.NumberPickerView_npv_AlternativeTextArrayWithMeasureHint -> {
                    mAlterTextArrayWithMeasureHint = a.getTextArray(attr)
                }
                R.styleable.NumberPickerView_npv_AlternativeTextArrayWithoutMeasureHint -> {
                    mAlterTextArrayWithoutMeasureHint = a.getTextArray(attr)
                }
                R.styleable.NumberPickerView_npv_RespondChangeOnDetached -> {
                    mRespondChangeOnDetach = a.getBoolean(attr, DEFAULT_RESPOND_CHANGE_ON_DETACH)
                }
                R.styleable.NumberPickerView_npv_RespondChangeInMainThread -> {
                    mRespondChangeInMainThread =
                        a.getBoolean(attr, DEFAULT_RESPOND_CHANGE_IN_MAIN_THREAD)
                }
                R.styleable.NumberPickerView_npv_TextEllipsize -> {
                    mTextEllipsize = a.getString(attr)
                }
                R.styleable.NumberPickerView_npv_SelectedItemBackground -> {
                    mSelectedItemBackground = a.getDrawable(attr)
                }
            }
        }
        a.recycle()
    }

    private fun init(context: Context) {
        mScroller = Scroller(context)
        mMiniVelocityFling = ViewConfiguration.get(getContext()).scaledMinimumFlingVelocity
        mScaledTouchSlop = ViewConfiguration.get(getContext()).scaledTouchSlop
        if (mTextSizeNormal == 0) {
            mTextSizeNormal = sp2px(context, DEFAULT_TEXT_SIZE_NORMAL_SP.toFloat())
        }
        if (mTextSizeSelected == 0) {
            mTextSizeSelected = sp2px(context, DEFAULT_TEXT_SIZE_SELECTED_SP.toFloat())
        }
        if (mTextSizeHint == 0) {
            mTextSizeHint = sp2px(context, DEFAULT_TEXT_SIZE_HINT_SP.toFloat())
        }
        if (mMarginStartOfHint == 0) {
            mMarginStartOfHint = dp2px(context, DEFAULT_MARGIN_START_OF_HINT_DP.toFloat())
        }
        if (mMarginEndOfHint == 0) {
            mMarginEndOfHint = dp2px(context, DEFAULT_MARGIN_END_OF_HINT_DP.toFloat())
        }
        mPaintDivider.color = mDividerColor
        mPaintDivider.isAntiAlias = true
        mPaintDivider.style = Paint.Style.STROKE
        mPaintDivider.strokeWidth = mDividerHeight.toFloat()
        mPaintText.color = mTextColorNormal
        mPaintText.isAntiAlias = true
        mPaintText.textAlign = Align.CENTER
        mPaintHint.color = mTextColorHint
        mPaintHint.isAntiAlias = true
        mPaintHint.textAlign = Align.CENTER
        mPaintHint.textSize = mTextSizeHint.toFloat()
        if (mShownCount % 2 == 0) {
            mShownCount++
        }
        if (mMinShowIndex == -1 || mMaxShowIndex == -1) {
            updateValueForInit()
        }
        initHandler()
    }

    private fun initHandler() {
        mHandlerThread = HandlerThread("HandlerThread-For-Refreshing")
        mHandlerThread?.start()
        mHandlerInNewThread = object : Handler(mHandlerThread!!.looper) {
            override fun handleMessage(msg: Message) {
                when (msg.what) {
                    HANDLER_WHAT_REFRESH -> {
                        if (mScroller?.isFinished == true) {
                            val willPickIndex = getWillPickIndexByGlobalY(mCurrDrawGlobalY)
                            val changeMsg =
                                getMsg(
                                    HANDLER_WHAT_LISTENER_VALUE_CHANGED,
                                    mPrevPickedIndex,
                                    willPickIndex,
                                    msg.obj
                                )
                            if (mRespondChangeInMainThread) {
                                mHandlerInMainThread?.sendMessageDelayed(
                                    changeMsg,
                                    (0 * 2).toLong()
                                )
                            } else {
                                mHandlerInNewThread?.sendMessageDelayed(changeMsg, (0 * 2).toLong())
                            }
                        } else {
                            var duration = 0
                            val willPickIndex: Int

                            // if scroller finished(not scrolling), then adjust the position
                            if (mCurrDrawFirstItemY != 0) { // need to adjust
                                if (mScrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                                    onScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                                }
                                if (mCurrDrawFirstItemY < -mItemHeight / 2) {
                                    // adjust to scroll upward
                                    duration =
                                        (
                                            DEFAULT_INTERVAL_REVISE_DURATION.toFloat() *
                                                (mItemHeight + mCurrDrawFirstItemY) / mItemHeight
                                            ).toInt()
                                    mScroller?.startScroll(
                                        0,
                                        mCurrDrawGlobalY,
                                        0,
                                        mItemHeight + mCurrDrawFirstItemY,
                                        duration * 3
                                    )
                                    willPickIndex =
                                        getWillPickIndexByGlobalY(
                                            mCurrDrawGlobalY + mItemHeight + mCurrDrawFirstItemY
                                        )
                                } else {
                                    // adjust to scroll downward
                                    duration =
                                        (
                                            DEFAULT_INTERVAL_REVISE_DURATION.toFloat() *
                                                -mCurrDrawFirstItemY / mItemHeight
                                            ).toInt()
                                    mScroller?.startScroll(
                                        0,
                                        mCurrDrawGlobalY,
                                        0,
                                        mCurrDrawFirstItemY,
                                        duration * 3
                                    )
                                    willPickIndex =
                                        getWillPickIndexByGlobalY(mCurrDrawGlobalY + mCurrDrawFirstItemY)
                                }
                                postInvalidate()
                            } else {
                                onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE)
                                // get the index which will be selected
                                willPickIndex = getWillPickIndexByGlobalY(mCurrDrawGlobalY)
                            }
                            val changeMsg =
                                getMsg(
                                    HANDLER_WHAT_LISTENER_VALUE_CHANGED,
                                    mPrevPickedIndex,
                                    willPickIndex,
                                    msg.obj
                                )
                            if (mRespondChangeInMainThread) {
                                mHandlerInMainThread?.sendMessageDelayed(
                                    changeMsg,
                                    (duration * 2).toLong()
                                )
                            } else {
                                mHandlerInNewThread?.sendMessageDelayed(
                                    changeMsg,
                                    (duration * 2).toLong()
                                )
                            }
                        }
                    }
                    HANDLER_WHAT_LISTENER_VALUE_CHANGED -> {
                        respondPickedValueChanged(
                            msg.arg1,
                            msg.arg2,
                            msg.obj
                        )
                    }
                }
            }
        }
        mHandlerInMainThread = object : Handler(Looper.getMainLooper()) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    HANDLER_WHAT_REQUEST_LAYOUT -> requestLayout()
                    HANDLER_WHAT_LISTENER_VALUE_CHANGED -> respondPickedValueChanged(
                        msg.arg1,
                        msg.arg2,
                        msg.obj
                    )
                }
            }
        }
    }

    private fun setDisplayedValuesAndPickedIndex(
        newDisplayedValues: Array<String?>?,
        pickedIndex: Int,
        needRefresh: Boolean
    ) {
        stopScrolling()
        if (newDisplayedValues == null) {
            throw IllegalArgumentException("newDisplayedValues should not be null.")
        }
        if (pickedIndex < 0) {
            throw IllegalArgumentException("pickedIndex should not be negative, now pickedIndex is $pickedIndex")
        }
        updateContent(newDisplayedValues)
        updateMaxWHOfDisplayedValues(true)
        updateNotWrapYLimit()
        updateValue()
        mPrevPickedIndex = pickedIndex + mMinShowIndex
        correctPositionByDefaultValue(pickedIndex, mWrapSelectorWheel && mWrapSelectorWheelCheck)
        if (needRefresh) {
            mHandlerInNewThread?.sendMessageDelayed(getMsg(HANDLER_WHAT_REFRESH), 0)
            postInvalidate()
        }
    }

    fun setDisplayedValues(newDisplayedValues: Array<String?>?, needRefresh: Boolean) {
        setDisplayedValuesAndPickedIndex(newDisplayedValues, 0, needRefresh)
    }

    /**
     * get the "fromValue" by using getValue(), if your picker's minValue is not 0,
     * make sure you can get the accurate value by getValue(), or you can use
     * smoothScrollToValue(int fromValue, int toValue, boolean needRespond)
     *
     * @param toValue the value you want picker to scroll to
     */
    fun smoothScrollToValue(toValue: Int) {
        smoothScrollToValue(value, toValue, true)
    }

    /**
     * get the "fromValue" by using getValue(), if your picker's minValue is not 0,
     * make sure you can get the accurate value by getValue(), or you can use
     * smoothScrollToValue(int fromValue, int toValue, boolean needRespond)
     *
     * @param toValue     the value you want picker to scroll to
     * @param needRespond set if you want picker to respond onValueChange listener
     */
    fun smoothScrollToValue(toValue: Int, needRespond: Boolean) {
        smoothScrollToValue(value, toValue, needRespond)
    }

    /**
     * @param fromValue   need to set the fromValue, can be greater than mMaxValue or less than mMinValue
     * @param toValue     the value you want picker to scroll to
     * @param needRespond need Respond to the ValueChange callback When Scrolling, default is false
     */
    @JvmOverloads
    fun smoothScrollToValue(fromValue: Int, toValue: Int, needRespond: Boolean = true) {
        var fromValue = fromValue
        var toValue = toValue
        var deltaIndex: Int
        fromValue = refineValueByLimit(
            fromValue, mMinValue, mMaxValue,
            mWrapSelectorWheel && mWrapSelectorWheelCheck
        )
        toValue = refineValueByLimit(
            toValue, mMinValue, mMaxValue,
            mWrapSelectorWheel && mWrapSelectorWheelCheck
        )
        if (mWrapSelectorWheel && mWrapSelectorWheelCheck) {
            deltaIndex = toValue - fromValue
            val halfOneRecycleSize = oneRecycleSize / 2
            if (deltaIndex < -halfOneRecycleSize || halfOneRecycleSize < deltaIndex) {
                deltaIndex =
                    if (deltaIndex > 0) deltaIndex - oneRecycleSize else deltaIndex + oneRecycleSize
            }
        } else {
            deltaIndex = toValue - fromValue
        }
        value = fromValue
        if (fromValue == toValue) {
            return
        }
        scrollByIndexSmoothly(deltaIndex, needRespond)
    }

    /**
     * simplify the "setDisplayedValue() + setMinValue() + setMaxValue()" process,
     * default minValue is 0, and make sure you do NOT change the minValue.
     *
     * @param display new values to be displayed
     */
    fun refreshByNewDisplayedValues(display: Array<String?>) {
        val minValue = minValue
        val oldMaxValue = maxValue
        val oldSpan = oldMaxValue - minValue + 1
        val newMaxValue = display.size - 1
        val newSpan = newMaxValue - minValue + 1
        if (newSpan > oldSpan) {
            displayedValues = display
            maxValue = newMaxValue
        } else {
            maxValue = newMaxValue
            displayedValues = display
        }
    }

    /**
     * used by handlers to respond onchange callbacks
     *
     * @param oldVal        prevPicked value
     * @param newVal        currPicked value
     * @param respondChange if want to respond onchange callbacks
     */
    private fun respondPickedValueChanged(oldVal: Int, newVal: Int, respondChange: Any?) {
        onScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE)
        if (oldVal != newVal &&
            ((respondChange == null) || respondChange !is Boolean || respondChange)
        ) {
            if (mOnValueChangeListener != null) {
                mOnValueChangeListener?.onValueChange(
                    this@NumberPickerView,
                    oldVal + mMinValue,
                    newVal + mMinValue
                )
            }
            if (mOnValueChangeListenerRaw != null) {
                mOnValueChangeListenerRaw?.onValueChangeRelativeToRaw(
                    this@NumberPickerView,
                    oldVal,
                    newVal,
                    mDisplayedValues
                )
            }
        }
        mPrevPickedIndex = newVal
        if (mPendingWrapToLinear) {
            mPendingWrapToLinear = false
            internalSetWrapToLinear()
        }
    }

    /**
     * @param deltaIndex  the delta index it will scroll by
     * @param needRespond need Respond to the ValueChange callback When Scrolling, default is false
     */
    private fun scrollByIndexSmoothly(deltaIndex: Int, needRespond: Boolean = true) {
        var deltaIndex = deltaIndex
        if (!(mWrapSelectorWheel && mWrapSelectorWheelCheck)) {
            val willPickRawIndex = pickedIndexRelativeToRaw
            if (willPickRawIndex + deltaIndex > mMaxShowIndex) {
                deltaIndex = mMaxShowIndex - willPickRawIndex
            } else if (willPickRawIndex + deltaIndex < mMinShowIndex) {
                deltaIndex = mMinShowIndex - willPickRawIndex
            }
        }
        var duration: Int
        var dy: Int
        if (mCurrDrawFirstItemY < (-mItemHeight / 2)) {
            dy = mItemHeight + mCurrDrawFirstItemY
            duration =
                (DEFAULT_INTERVAL_REVISE_DURATION.toFloat() * (mItemHeight + mCurrDrawFirstItemY) / mItemHeight).toInt()
            duration = if (deltaIndex < 0) {
                -duration - deltaIndex * DEFAULT_INTERVAL_REVISE_DURATION
            } else {
                deltaIndex * DEFAULT_INTERVAL_REVISE_DURATION
            }
        } else {
            dy = mCurrDrawFirstItemY
            duration =
                (DEFAULT_INTERVAL_REVISE_DURATION.toFloat() * (-mCurrDrawFirstItemY) / mItemHeight).toInt()
            if (deltaIndex < 0) {
                duration -= deltaIndex * DEFAULT_INTERVAL_REVISE_DURATION
            } else {
                duration += deltaIndex * DEFAULT_INTERVAL_REVISE_DURATION
            }
        }
        dy += deltaIndex * mItemHeight
        if (duration < DEFAULT_MIN_SCROLL_BY_INDEX_DURATION) {
            duration = DEFAULT_MIN_SCROLL_BY_INDEX_DURATION
        }
        if (duration > DEFAULT_MAX_SCROLL_BY_INDEX_DURATION) {
            duration = DEFAULT_MAX_SCROLL_BY_INDEX_DURATION
        }
        mScroller?.startScroll(0, mCurrDrawGlobalY, 0, dy, duration)
        if (needRespond) {
            mHandlerInNewThread?.sendMessageDelayed(
                getMsg(HANDLER_WHAT_REFRESH),
                (duration / 4).toLong()
            )
        } else {
            mHandlerInNewThread?.sendMessageDelayed(
                getMsg(HANDLER_WHAT_REFRESH, 0, 0, needRespond),
                (duration / 4).toLong()
            )
        }
        postInvalidate()
    }

    private fun setMinAndMaxShowIndex(minShowIndex: Int, maxShowIndex: Int) {
        setMinAndMaxShowIndex(minShowIndex, maxShowIndex, true)
    }

    private fun setMinAndMaxShowIndex(minShowIndex: Int, maxShowIndex: Int, needRefresh: Boolean) {
        if (minShowIndex > maxShowIndex) {
            throw IllegalArgumentException(
                (
                    "minShowIndex should be less than maxShowIndex, minShowIndex is " +
                        minShowIndex + ", maxShowIndex is " + maxShowIndex + "."
                    )
            )
        }
        if (mDisplayedValues == null) {
            throw IllegalArgumentException(
                "mDisplayedValues should not be null, you need to set mDisplayedValues first."
            )
        } else {
            if (minShowIndex < 0) {
                throw IllegalArgumentException(
                    "minShowIndex should not be less than 0, now minShowIndex is $minShowIndex"
                )
            } else if (minShowIndex > mDisplayedValues?.size ?: 0 - 1) {
                throw IllegalArgumentException(
                    (
                        "minShowIndex should not be greater than (mDisplayedValues.length - 1), now " +
                            "(mDisplayedValues.length - 1) is " +
                            (mDisplayedValues?.size?.minus(1)) +
                            " minShowIndex is " +
                            minShowIndex
                        )
                )
            }
            if (maxShowIndex < 0) {
                throw IllegalArgumentException(
                    "maxShowIndex should not be less than 0, now maxShowIndex is $maxShowIndex"
                )
            } else if (maxShowIndex > mDisplayedValues?.size ?: 0 - 1) {
                throw IllegalArgumentException(
                    (
                        "maxShowIndex should not be greater than (mDisplayedValues.length - 1), now " +
                            "(mDisplayedValues.length - 1) is " +
                            (mDisplayedValues?.size?.minus(1)) +
                            " maxShowIndex is " +
                            maxShowIndex
                        )
                )
            }
        }
        mMinShowIndex = minShowIndex
        mMaxShowIndex = maxShowIndex
        if (needRefresh) {
            mPrevPickedIndex = 0 + mMinShowIndex
            correctPositionByDefaultValue(0, mWrapSelectorWheel && mWrapSelectorWheelCheck)
            postInvalidate()
        }
    }

    /**
     * set the friction of scroller, it will effect the scroller's acceleration when fling
     *
     * @param friction default is ViewConfiguration.get(mContext).getScrollFriction()
     * if setFriction(2 * ViewConfiguration.get(mContext).getScrollFriction()),
     * the friction will be twice as much as before
     */
    fun setFriction(friction: Float) {
        if (friction <= 0) {
            throw IllegalArgumentException("you should set a a positive float friction, now friction is $friction")
        }
        mFriction = ViewConfiguration.getScrollFriction() / friction
    }

    // compatible for NumberPicker
    private fun onScrollStateChange(scrollState: Int) {
        if (mScrollState == scrollState) {
            return
        }
        mScrollState = scrollState
        if (mOnScrollListener != null) {
            mOnScrollListener?.onScrollStateChange(this, scrollState)
        }
    }

    // compatible for NumberPicker
    fun setOnScrollListener(listener: OnScrollListener?) {
        mOnScrollListener = listener
    }

    // compatible for NumberPicker
    fun setOnValueChangedListener(listener: OnValueChangeListener?) {
        mOnValueChangeListener = listener
    }

    // return index relative to mDisplayedValues from 0.
    private fun getWillPickIndexByGlobalY(globalY: Int): Int {
        if (mItemHeight == 0) {
            return 0
        }
        val willPickIndex = globalY / mItemHeight + mShownCount / 2
        val index = getIndexByRawIndex(
            willPickIndex,
            oneRecycleSize,
            mWrapSelectorWheel && mWrapSelectorWheelCheck
        )
        return if (index in 0 until oneRecycleSize) {
            index + mMinShowIndex
        } else {
            0
        }
    }

    private fun getIndexByRawIndex(index: Int, size: Int, wrap: Boolean): Int {
        var index = index
        if (size <= 0) {
            return 0
        }
        return if (wrap) {
            index %= size
            if (index < 0) {
                index += size
            }
            index
        } else {
            index
        }
    }

    private fun internalSetWrapToLinear() {
        val rawIndex = pickedIndexRelativeToRaw
        correctPositionByDefaultValue(rawIndex - mMinShowIndex, false)
        mWrapSelectorWheel = false
        postInvalidate()
    }

    private fun updateDividerAttr() {
        mDividerIndex0 = mShownCount / 2
        mDividerIndex1 = mDividerIndex0 + 1
        dividerY0 = (mDividerIndex0 * mViewHeight / mShownCount).toFloat()
        dividerY1 = (mDividerIndex1 * mViewHeight / mShownCount).toFloat()
        if (mDividerMarginL < 0) {
            mDividerMarginL = 0
        }
        if (mDividerMarginR < 0) {
            mDividerMarginR = 0
        }
        if (mDividerMarginL + mDividerMarginR == 0) {
            return
        }
        if (paddingLeft + mDividerMarginL >= mViewWidth - paddingRight - mDividerMarginR) {
            val surplusMargin =
                paddingLeft + mDividerMarginL + paddingRight + mDividerMarginR - mViewWidth
            mDividerMarginL =
                (
                    mDividerMarginL - surplusMargin.toFloat() *
                        mDividerMarginL / (mDividerMarginL + mDividerMarginR)
                    ).toInt()
            mDividerMarginR =
                (
                    mDividerMarginR - surplusMargin.toFloat() *
                        mDividerMarginR / (mDividerMarginL + mDividerMarginR)
                    ).toInt()
        }
    }

    private fun updateFontAttr() {
        if (mTextSizeNormal > mItemHeight) {
            mTextSizeNormal = mItemHeight
        }
        if (mTextSizeSelected > mItemHeight) {
            mTextSizeSelected = mItemHeight
        }
        mPaintHint.textSize = mTextSizeHint.toFloat()
        mTextSizeHintCenterYOffset = getTextCenterYOffset(mPaintHint.fontMetrics)
        mWidthOfHintText = getTextWidth(mHintText, mPaintHint)
        mPaintText.textSize = mTextSizeSelected.toFloat()
        mTextSizeSelectedCenterYOffset = getTextCenterYOffset(mPaintText.fontMetrics)
        mPaintText.textSize = mTextSizeNormal.toFloat()
        mTextSizeNormalCenterYOffset = getTextCenterYOffset(mPaintText.fontMetrics)
    }

    private fun updateNotWrapYLimit() {
        mNotWrapLimitYTop = 0
        mNotWrapLimitYBottom = -mShownCount * mItemHeight
        if (mDisplayedValues != null) {
            mNotWrapLimitYTop = (oneRecycleSize - (mShownCount / 2) - 1) * mItemHeight
            mNotWrapLimitYBottom = -(mShownCount / 2) * mItemHeight
        }
    }

    private fun limitY(currDrawGlobalYPreferred: Int): Int {
        var currDrawGlobalYPreferred = currDrawGlobalYPreferred
        if (mWrapSelectorWheel && mWrapSelectorWheelCheck) {
            return currDrawGlobalYPreferred
        }
        if (currDrawGlobalYPreferred < mNotWrapLimitYBottom) {
            currDrawGlobalYPreferred = mNotWrapLimitYBottom
        } else if (currDrawGlobalYPreferred > mNotWrapLimitYTop) {
            currDrawGlobalYPreferred = mNotWrapLimitYTop
        }
        return currDrawGlobalYPreferred
    }

    private fun click(event: MotionEvent) {
        val y = event.y
        for (i in 0 until mShownCount) {
            if (mItemHeight * i <= y && y < mItemHeight * (i + 1)) {
                clickItem(i)
                break
            }
        }
    }

    private fun clickItem(showCountIndex: Int) {
        if (showCountIndex in 0 until mShownCount) {
            // clicked the showCountIndex of the view
            scrollByIndexSmoothly(showCountIndex - mShownCount / 2)
        } else {
            // wrong
        }
    }

    private fun getTextCenterYOffset(fontMetrics: Paint.FontMetrics?): Float {
        return if (fontMetrics == null) {
            0f
        } else Math.abs(fontMetrics.top + fontMetrics.bottom) / 2
    }

    // defaultPickedIndex relative to the shown part
    private fun correctPositionByDefaultValue(defaultPickedIndex: Int, wrap: Boolean) {
        mCurrDrawFirstItemIndex = defaultPickedIndex - (mShownCount - 1) / 2
        mCurrDrawFirstItemIndex = getIndexByRawIndex(mCurrDrawFirstItemIndex, oneRecycleSize, wrap)
        if (mItemHeight == 0) {
            mCurrentItemIndexEffect = true
        } else {
            mCurrDrawGlobalY = mCurrDrawFirstItemIndex * mItemHeight
            mInScrollingPickedOldValue = mCurrDrawFirstItemIndex + mShownCount / 2
            mInScrollingPickedOldValue = mInScrollingPickedOldValue % oneRecycleSize
            if (mInScrollingPickedOldValue < 0) {
                mInScrollingPickedOldValue = mInScrollingPickedOldValue + oneRecycleSize
            }
            mInScrollingPickedNewValue = mInScrollingPickedOldValue
            calculateFirstItemParameterByGlobalY()
        }
    }

    override fun computeScroll() {
        if (mItemHeight == 0) {
            return
        }
        if (mScroller?.computeScrollOffset() == true) {
            mCurrDrawGlobalY = mScroller?.currY ?: 0
            calculateFirstItemParameterByGlobalY()
            postInvalidate()
        }
    }

    private fun calculateFirstItemParameterByGlobalY() {
        mCurrDrawFirstItemIndex =
            Math.floor((mCurrDrawGlobalY.toFloat() / mItemHeight).toDouble()).toInt()
        mCurrDrawFirstItemY = -(mCurrDrawGlobalY - mCurrDrawFirstItemIndex * mItemHeight)
        if (mOnValueChangeListenerInScrolling != null) {
            if (-mCurrDrawFirstItemY > mItemHeight / 2) {
                mInScrollingPickedNewValue = mCurrDrawFirstItemIndex + 1 + (mShownCount / 2)
            } else {
                mInScrollingPickedNewValue = mCurrDrawFirstItemIndex + mShownCount / 2
            }
            mInScrollingPickedNewValue = mInScrollingPickedNewValue % oneRecycleSize
            if (mInScrollingPickedNewValue < 0) {
                mInScrollingPickedNewValue = mInScrollingPickedNewValue + oneRecycleSize
            }
            if (mInScrollingPickedOldValue != mInScrollingPickedNewValue) {
                respondPickedValueChangedInScrolling(
                    mInScrollingPickedOldValue + mMinValue,
                    mInScrollingPickedNewValue + mMinValue
                )
            }
            mInScrollingPickedOldValue = mInScrollingPickedNewValue
        }
    }

    private fun releaseVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker?.clear()
            mVelocityTracker?.recycle()
            mVelocityTracker = null
        }
    }

    private fun updateMaxWHOfDisplayedValues(needRequestLayout: Boolean) {
        updateMaxWidthOfDisplayedValues()
        updateMaxHeightOfDisplayedValues()
        if (needRequestLayout &&
            (mSpecModeW == MeasureSpec.AT_MOST || mSpecModeH == MeasureSpec.AT_MOST)
        ) {
            mHandlerInMainThread?.sendEmptyMessage(HANDLER_WHAT_REQUEST_LAYOUT)
        }
    }

    private fun measureWidth(measureSpec: Int): Int {
        var result: Int
        mSpecModeW = MeasureSpec.getMode(measureSpec)
        val specMode = mSpecModeW
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            val marginOfHint =
                if (mWidthOfHintText.coerceAtLeast(mWidthOfAlterHint) == 0) 0 else mMarginEndOfHint
            val gapOfHint =
                if (mWidthOfHintText.coerceAtLeast(mWidthOfAlterHint) == 0) 0 else mMarginStartOfHint
            val maxWidth = mMaxWidthOfAlterArrayWithMeasureHint.coerceAtLeast(
                (
                    mMaxWidthOfDisplayedValues.coerceAtLeast(
                        mMaxWidthOfAlterArrayWithoutMeasureHint
                    ) +
                        2 * (
                        gapOfHint + mWidthOfHintText.coerceAtLeast(mWidthOfAlterHint) +
                            marginOfHint +
                            (2 * mItemPaddingHorizontal)
                        )
                    )
            )
            result = this.paddingLeft + this.paddingRight + maxWidth // MeasureSpec.UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = result.coerceAtMost(specSize)
            }
        }
        return result
    }

    private fun measureHeight(measureSpec: Int): Int {
        var result: Int
        mSpecModeH = MeasureSpec.getMode(measureSpec)
        val specMode = mSpecModeH
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            val maxHeight = mShownCount * (mMaxHeightOfDisplayedValues + 2 * mItemPaddingVertical)
            result = this.paddingTop + this.paddingBottom + maxHeight // MeasureSpec.UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize)
            }
        }
        return result
    }

    private fun drawSelectedItemBackground(canvas: Canvas) {
        if (mSelectedItemBackground != null) {
            canvas.save()
            val left = paddingLeft + mDividerMarginL
            val top = dividerY0.toInt()
            val right = mViewWidth - paddingRight - mDividerMarginR
            val bottom = dividerY1.toInt()
            mSelectedItemBackground?.setBounds(left, top, right, bottom)
            mSelectedItemBackground?.draw(canvas)
            canvas.restore()
        }
    }

    private fun drawContent(canvas: Canvas) {
        var index: Int
        var textColor: Int
        var textSize: Float
        var fraction = 0f // fraction of the item in state between normal and selected, in[0, 1]
        var textSizeCenterYOffset: Float
        for (i in 0 until mShownCount + 1) {
            val y = (mCurrDrawFirstItemY + mItemHeight * i).toFloat()
            index = getIndexByRawIndex(
                mCurrDrawFirstItemIndex + i,
                oneRecycleSize, mWrapSelectorWheel && mWrapSelectorWheelCheck
            )
            when (i) {
                mShownCount / 2 -> { // this will be picked
                    fraction = (mItemHeight + mCurrDrawFirstItemY).toFloat() / mItemHeight
                    textColor = getEvaluateColor(fraction, mTextColorNormal, mTextColorSelected)
                    textSize = getEvaluateSize(
                        fraction,
                        mTextSizeNormal.toFloat(),
                        mTextSizeSelected.toFloat()
                    )
                    textSizeCenterYOffset = getEvaluateSize(
                        fraction, mTextSizeNormalCenterYOffset,
                        mTextSizeSelectedCenterYOffset
                    )
                }
                mShownCount / 2 + 1 -> {
                    textColor = getEvaluateColor(1 - fraction, mTextColorNormal, mTextColorSelected)
                    textSize =
                        getEvaluateSize(
                            1 - fraction,
                            mTextSizeNormal.toFloat(),
                            mTextSizeSelected.toFloat()
                        )
                    textSizeCenterYOffset = getEvaluateSize(
                        1 - fraction, mTextSizeNormalCenterYOffset,
                        mTextSizeSelectedCenterYOffset
                    )
                }
                else -> {
                    textColor = mTextColorNormal
                    textSize = mTextSizeNormal.toFloat()
                    textSizeCenterYOffset = mTextSizeNormalCenterYOffset
                }
            }
            mPaintText.color = textColor
            mPaintText.textSize = textSize
            if (index in 0 until oneRecycleSize) {
                var str: CharSequence? = mDisplayedValues?.get(index + mMinShowIndex)
                if (mTextEllipsize != null) {
                    str = TextUtils.ellipsize(
                        str, mPaintText, (width - 2 * mItemPaddingHorizontal).toFloat(),
                        ellipsizeType
                    )
                }
                mPaintText.let {
                    canvas.drawText(
                        str.toString(), mViewCenterX,
                        y + (mItemHeight / 2) + textSizeCenterYOffset, it
                    )
                }
            } else if (!TextUtils.isEmpty(mEmptyItemHint)) {
                mEmptyItemHint?.let {
                    mPaintText.let { it1 ->
                        canvas.drawText(
                            it, mViewCenterX,
                            y + (mItemHeight / 2) + textSizeCenterYOffset, it1
                        )
                    }
                }
            }
        }
    }

    private fun drawLine(canvas: Canvas) {
        if (mShowDivider) {
            canvas.drawLine(
                (paddingLeft + mDividerMarginL).toFloat(),
                dividerY0,
                (mViewWidth - paddingRight - mDividerMarginR).toFloat(),
                dividerY0,
                mPaintDivider
            )
            canvas.drawLine(
                (paddingLeft + mDividerMarginL).toFloat(),
                dividerY1,
                (mViewWidth - paddingRight - mDividerMarginR).toFloat(),
                dividerY1,
                mPaintDivider
            )
        }
    }

    private fun drawHint(canvas: Canvas) {
        if (TextUtils.isEmpty(mHintText)) {
            return
        }
        mHintText?.let {
            canvas.drawText(
                it,
                mViewCenterX + ((mMaxWidthOfDisplayedValues + mWidthOfHintText) / 2) + mMarginStartOfHint,
                (dividerY0 + dividerY1) / 2 + mTextSizeHintCenterYOffset, mPaintHint
            )
        }
    }

    private fun updateMaxWidthOfDisplayedValues() {
        val savedTextSize = mPaintText.textSize
        mPaintText.textSize = mTextSizeSelected.toFloat()
        mMaxWidthOfDisplayedValues =
            getMaxWidthOfTextArray(mDisplayedValues as? Array<CharSequence?>?, mPaintText)
        mMaxWidthOfAlterArrayWithMeasureHint =
            getMaxWidthOfTextArray(mAlterTextArrayWithMeasureHint, mPaintText)
        mMaxWidthOfAlterArrayWithoutMeasureHint =
            getMaxWidthOfTextArray(mAlterTextArrayWithoutMeasureHint, mPaintText)
        mPaintText.textSize = mTextSizeHint.toFloat()
        mWidthOfAlterHint = getTextWidth(mAlterHint, mPaintText)
        mPaintText.textSize = savedTextSize
    }

    private fun getMaxWidthOfTextArray(array: Array<CharSequence?>?, paint: Paint?): Int {
        if (array == null) {
            return 0
        }
        var maxWidth = 0
        for (item: CharSequence? in array) {
            if (item != null) {
                val itemWidth = getTextWidth(item, paint)
                maxWidth = itemWidth.coerceAtLeast(maxWidth)
            }
        }
        return maxWidth
    }

    private fun getTextWidth(text: CharSequence?, paint: Paint?): Int {
        if (TextUtils.isEmpty(text)) {
            return 0
        }
        val key = text.toString()
        if (mTextWidthCache.containsKey(key)) {
            val integer = mTextWidthCache[key]
            if (integer != null) {
                return integer
            }
        }
        val value = ((paint?.measureText(key) ?: 0f) + 0.5f).toInt()
        mTextWidthCache[key] = value
        return value
    }

    private fun updateMaxHeightOfDisplayedValues() {
        val savedTextSize = mPaintText.textSize
        mPaintText.textSize = mTextSizeSelected.toFloat()
        mMaxHeightOfDisplayedValues =
            (
                (mPaintText.fontMetrics?.bottom ?: 0f) -
                    (mPaintText.fontMetrics?.top ?: 0f) +
                    0.5
                ).toInt()
        mPaintText.textSize = savedTextSize
    }

    private fun updateContent(newDisplayedValues: Array<String?>) {
        mDisplayedValues = newDisplayedValues
        updateWrapStateByContent()
    }

    // used in setDisplayedValues
    private fun updateValue() {
        inflateDisplayedValuesIfNull()
        updateWrapStateByContent()
        mMinShowIndex = 0
        mMaxShowIndex = mDisplayedValues?.size ?: 0 - 1
    }

    private fun updateValueForInit() {
        inflateDisplayedValuesIfNull()
        updateWrapStateByContent()
        if (mMinShowIndex == -1) {
            mMinShowIndex = 0
        }
        if (mMaxShowIndex == -1) {
            mMaxShowIndex = mDisplayedValues?.size ?: 0 - 1
        }
        setMinAndMaxShowIndex(mMinShowIndex, mMaxShowIndex, false)
    }

    private fun inflateDisplayedValuesIfNull() {
        if (mDisplayedValues == null) {
            mDisplayedValues = arrayOfNulls(1)
            mDisplayedValues!![0] = "0"
        }
    }

    private fun updateWrapStateByContent() {
        mWrapSelectorWheelCheck = mDisplayedValues?.size ?: 0 > mShownCount
    }

    private fun refineValueByLimit(value: Int, minValue: Int, maxValue: Int, wrap: Boolean): Int {
        var value = value
        if (wrap) {
            if (value > maxValue) {
                value = (value - maxValue) % oneRecycleSize + minValue - 1
            } else if (value < minValue) {
                value = ((value - minValue) % oneRecycleSize) + maxValue + 1
            }
            return value
        } else {
            if (value > maxValue) {
                value = maxValue
            } else if (value < minValue) {
                value = minValue
            }
            return value
        }
    }

    private fun stopRefreshing() {
        if (mHandlerInNewThread != null) {
            mHandlerInNewThread?.removeMessages(HANDLER_WHAT_REFRESH)
        }
    }

    private fun stopScrolling() {
        if (mScroller != null && mScroller?.isFinished == false) {
            mScroller?.startScroll(0, mScroller?.currY ?: 0, 0, 0, 1)
            mScroller?.abortAnimation()
            postInvalidate()
        }
    }

    fun stopScrollingAndCorrectPosition() {
        stopScrolling()
        if (mHandlerInNewThread != null) {
            mHandlerInNewThread?.sendMessageDelayed(getMsg(HANDLER_WHAT_REFRESH), 0)
        }
    }

    private fun getMsg(what: Int): Message {
        return getMsg(what, 0, 0, null)
    }

    private fun getMsg(what: Int, arg1: Int, arg2: Int, obj: Any?): Message {
        val msg = Message.obtain()
        msg.what = what
        msg.arg1 = arg1
        msg.arg2 = arg2
        msg.obj = obj
        return msg
    }

    private fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    private fun dp2px(context: Context, dpValue: Float): Int {
        val densityScale = context.resources.displayMetrics.density
        return (dpValue * densityScale + 0.5f).toInt()
    }

    private fun getEvaluateColor(fraction: Float, startColor: Int, endColor: Int): Int {
        val a: Int
        val r: Int
        val g: Int
        val b: Int
        val sA = (startColor and -0x1000000) ushr 24
        val sR = (startColor and 0x00ff0000) ushr 16
        val sG = (startColor and 0x0000ff00) ushr 8
        val sB = (startColor and 0x000000ff) ushr 0
        val eA = (endColor and -0x1000000) ushr 24
        val eR = (endColor and 0x00ff0000) ushr 16
        val eG = (endColor and 0x0000ff00) ushr 8
        val eB = (endColor and 0x000000ff) ushr 0
        a = (sA + (eA - sA) * fraction).toInt()
        r = (sR + (eR - sR) * fraction).toInt()
        g = (sG + (eG - sG) * fraction).toInt()
        b = (sB + (eB - sB) * fraction).toInt()
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }

    private fun getEvaluateSize(fraction: Float, startSize: Float, endSize: Float): Float {
        return startSize + (endSize - startSize) * fraction
    }

    private fun convertCharSequenceArrayToStringArray(charSequences: Array<CharSequence>?): Array<String?>? {
        if (charSequences == null) {
            return null
        }
        val ret = arrayOfNulls<String>(charSequences.size)
        for (i in charSequences.indices) {
            ret[i] = charSequences[i].toString()
        }
        return ret
    }

    // compatible for NumberPicker
    interface OnValueChangeListener {
        fun onValueChange(picker: NumberPickerView?, oldVal: Int, newVal: Int)
    }

    interface OnValueChangeListenerRelativeToRaw {
        fun onValueChangeRelativeToRaw(
            picker: NumberPickerView?,
            oldPickedIndex: Int,
            newPickedIndex: Int,
            displayedValues: Array<String?>?
        )
    }

    interface OnValueChangeListenerInScrolling {
        fun onValueChangeInScrolling(picker: NumberPickerView?, oldVal: Int, newVal: Int)
    }

    // compatible for NumberPicker
    interface OnScrollListener {
        fun onScrollStateChange(view: NumberPickerView?, scrollState: Int)

        companion object {
            const val SCROLL_STATE_IDLE = 0
            const val SCROLL_STATE_TOUCH_SCROLL = 1
            const val SCROLL_STATE_FLING = 2
        }
    }

    companion object {
        // default text color of not selected item
        private const val DEFAULT_TEXT_COLOR_NORMAL = -0xcccccd

        // default text color of selected item
        private const val DEFAULT_TEXT_COLOR_SELECTED = -0xa9ced

        // default text size of normal item
        private const val DEFAULT_TEXT_SIZE_NORMAL_SP = 14

        // default text size of selected item
        private const val DEFAULT_TEXT_SIZE_SELECTED_SP = 16

        // default text size of hint text, the middle item's right text
        private const val DEFAULT_TEXT_SIZE_HINT_SP = 14

        // distance between selected text and hint text
        private const val DEFAULT_MARGIN_START_OF_HINT_DP = 8

        // distance between hint text and right of this view, used in wrap_content mode
        private const val DEFAULT_MARGIN_END_OF_HINT_DP = 8

        // default divider's color
        private const val DEFAULT_DIVIDER_COLOR = -0xa9ced

        // default divider's height
        private const val DEFAULT_DIVIDER_HEIGHT = 2

        // default divider's margin to the left & right of this view
        private const val DEFAULT_DIVIDER_MARGIN_HORIZONTAL = 0

        // default shown items' count, now we display 3 items, the 2nd one is selected
        private const val DEFAULT_SHOWN_COUNT = 3

        // default items' horizontal padding, left padding and right padding are both 5dp,
        // only used in wrap_content mode
        private const val DEFAULT_ITEM_PADDING_DP_H = 5

        // default items' vertical padding, top padding and bottom padding are both 2dp,
        // only used in wrap_content mode
        private const val DEFAULT_ITEM_PADDING_DP_V = 2

        // message's what argument to refresh current state, used by mHandler
        private const val HANDLER_WHAT_REFRESH = 1

        // message's what argument to respond value changed event, used by mHandler
        private const val HANDLER_WHAT_LISTENER_VALUE_CHANGED = 2

        // message's what argument to request layout, used by mHandlerInMainThread
        private const val HANDLER_WHAT_REQUEST_LAYOUT = 3

        // in millisecond unit, default duration of scrolling an item' distance
        private const val DEFAULT_INTERVAL_REVISE_DURATION = 300

        // max and min durations when scrolling from one value to another
        private const val DEFAULT_MIN_SCROLL_BY_INDEX_DURATION =
            DEFAULT_INTERVAL_REVISE_DURATION * 1
        private const val DEFAULT_MAX_SCROLL_BY_INDEX_DURATION =
            DEFAULT_INTERVAL_REVISE_DURATION * 2
        private const val TEXT_ELLIPSIZE_START = "start"
        private const val TEXT_ELLIPSIZE_MIDDLE = "middle"
        private const val TEXT_ELLIPSIZE_END = "end"
        private const val DEFAULT_SHOW_DIVIDER = true
        private const val DEFAULT_WRAP_SELECTOR_WHEEL = true
        private const val DEFAULT_CURRENT_ITEM_INDEX_EFFECT = false
        private const val DEFAULT_RESPOND_CHANGE_ON_DETACH = false
        private const val DEFAULT_RESPOND_CHANGE_IN_MAIN_THREAD = true
    }
}
