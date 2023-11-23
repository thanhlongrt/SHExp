package com.example.salehub.slide_bar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.salehub.R
import kotlin.math.min
import kotlin.math.roundToInt

class SlideBar : RelativeLayout {

    companion object {
        const val DEFAULT_MAX_VALUE = 100F
        const val DEFAULT_MIN_VALUE = 0F
        const val DEFAULT_TEXT_SIZE = 14F
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    init {
        setWillNotDraw(false)
    }

    private var mThumb: TextView? = null

    private var mBubbleView: BubbleView? = null

    private val mProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.colorGreen)
        style = Paint.Style.FILL
    }

    private val textPaintMinValue = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
        textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            DEFAULT_TEXT_SIZE,
            resources.displayMetrics
        )
        textAlign = Paint.Align.LEFT
        typeface = ResourcesCompat.getFont(context, R.font.inter_medium)
    }

    private val textPaintMaxValue = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK
        textSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            DEFAULT_TEXT_SIZE,
            resources.displayMetrics
        )
        textAlign = Paint.Align.RIGHT
        typeface = ResourcesCompat.getFont(context, R.font.inter_medium)
    }

    private var thumbWidth = resources.getDimensionPixelOffset(R.dimen._50dp)
    private var thumbHeight = resources.getDimensionPixelOffset(R.dimen._40dp)
    private var thumbPaddingHorizontal = resources.getDimensionPixelOffset(R.dimen._8dp)
    private var thumbPaddingVertical = resources.getDimensionPixelOffset(R.dimen._4dp)

    private var progressHeight = resources.getDimensionPixelOffset(R.dimen._20dp)
    private var bubbleHeight = resources.getDimensionPixelOffset(R.dimen._44dp)
    private var bubbleWidth = resources.getDimensionPixelOffset(R.dimen._48dp)
    private var bubbleMarginBottom = resources.getDimensionPixelOffset(R.dimen._8dp)


    private var progressPaddingHorizontal = resources.getDimensionPixelOffset(R.dimen._30dp)
    private var paddingHorizontal = resources.getDimensionPixelOffset(R.dimen._16dp)

    private var tvMaxValueRect: Rect = Rect()
    private var tvMinValueRect: Rect = Rect()

    private var slideBarWidth: Int = 0

    var progressRadius = resources.getDimensionPixelOffset(R.dimen._9dp).toFloat()


    var maxValue: Float = DEFAULT_MAX_VALUE
    var minValue: Float = DEFAULT_MIN_VALUE
    var currentValue: Float = DEFAULT_MIN_VALUE

    var progressRatio: Float = 0.0F
        set(value) {
            currentValue = value * (maxValue - minValue)
            field = value
            updateThumb()
            updateBubbleView()
            invalidate()
        }


    private fun drawBackground(canvas: Canvas?) {
        ContextCompat.getDrawable(context, R.drawable.border_shadow)?.apply {
            setBounds(
                paddingHorizontal + tvMinValueRect.width() + progressPaddingHorizontal,
                bubbleHeight + bubbleMarginBottom + (thumbHeight - progressHeight) / 2,
                width - paddingHorizontal - tvMaxValueRect.width() - progressPaddingHorizontal,
                bubbleHeight + bubbleMarginBottom + progressHeight + (thumbHeight - progressHeight) / 2
            )
            canvas?.let(::draw)
        }
    }

    private fun drawProgressShape(canvas: Canvas?) {
        slideBarWidth =
            width - 2 * progressPaddingHorizontal - 2 * paddingHorizontal - tvMinValueRect.width() - tvMaxValueRect.width()
        val rectF = RectF().apply {
            left = 0f + paddingHorizontal + tvMinValueRect.width() + progressPaddingHorizontal
            top = 0f + bubbleHeight + bubbleMarginBottom + (thumbHeight - progressHeight) / 2
            right =
                slideBarWidth * progressRatio + paddingHorizontal + tvMinValueRect.width() + progressPaddingHorizontal
            bottom =
                0f + bubbleHeight + bubbleMarginBottom + progressHeight + (thumbHeight - progressHeight) / 2
        }
        canvas?.drawRoundRect(rectF, progressRadius, progressRadius, mProgressPaint)
    }

    private fun drawThumb() {
        if (mThumb != null) return
        mThumb = TextView(context).apply {
            val params =
                LayoutParams(thumbWidth, thumbHeight)
            layoutParams = params
            setTextSize(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE)
            gravity = Gravity.CENTER
            textAlignment = TEXT_ALIGNMENT_CENTER
            setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
            text = currentValue.roundToInt().toString()
            background = ContextCompat.getDrawable(context, R.drawable.bg_thumb)
            setPadding(
                thumbPaddingHorizontal, thumbPaddingVertical,
                thumbPaddingHorizontal, thumbPaddingVertical
            )
            addView(this)
        }
        mThumb?.y =
            (bubbleHeight + bubbleMarginBottom).toFloat() - progressHeight / 2 + (thumbHeight - progressHeight) / 2
        updateThumb()
    }

    private fun updateThumb() {
        mThumb?.text = formatValue(currentValue)
        mThumb?.x =
            progressRatio * slideBarWidth + paddingHorizontal + tvMinValueRect.width() + progressPaddingHorizontal - thumbWidth / 2
    }

    private fun drawBubbleView() {
        if (mBubbleView != null) return
        mBubbleView = BubbleView(context).apply {
            layoutParams = LayoutParams(bubbleWidth, bubbleHeight)
            addView(this)
        }
        updateBubbleView()
    }

    private fun updateBubbleView() {
        mBubbleView?.setContentString(formatValue(currentValue))
        mBubbleView?.x =
            progressRatio * slideBarWidth + paddingHorizontal + tvMinValueRect.width() + progressPaddingHorizontal - bubbleWidth / 2
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                updateProgressRatio(event.x)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                updateProgressRatio(event.x)
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    private fun updateProgressRatio(x: Float) {
        progressRatio = when {
            x <= progressPaddingHorizontal -> {
                0f
            }
            x >= width - progressPaddingHorizontal -> {
                1f
            }
            else -> {
                (x - progressPaddingHorizontal) / (width - 2 * progressPaddingHorizontal)
            }
        }
    }

    private fun drawTextViews(canvas: Canvas?) {
        drawTextViewMinValue(minValue, canvas)
        drawTextViewMaxValue(maxValue, canvas)
    }

    private fun drawTextViewMaxValue(value: Float, canvas: Canvas?) {
        calculateTvMaxValueRect()
        canvas?.drawText(
            formatValue(value) + "L",
            width - paddingHorizontal.toFloat(),
            (bubbleHeight + bubbleMarginBottom + bubbleHeight / 2 + tvMaxValueRect.height() / 2).toFloat(),
            textPaintMaxValue
        )
    }

    private fun drawTextViewMinValue(value: Float, canvas: Canvas?) {
        calculateMinValueTextViewRect()
        canvas?.drawText(
            formatValue(value) + "L",
            paddingHorizontal.toFloat(),
            (bubbleHeight + bubbleMarginBottom + bubbleHeight / 2 + tvMinValueRect.height() / 2).toFloat(),
            textPaintMinValue
        )
    }

    private fun calculateMinValueTextViewRect() {
        val stringValue = formatValue(minValue) + "L"
        textPaintMinValue.getTextBounds(stringValue, 0, stringValue.length, tvMinValueRect)
    }

    private fun calculateTvMaxValueRect() {
        val stringValue = formatValue(maxValue) + "L"
        textPaintMaxValue.getTextBounds(stringValue, 0, stringValue.length, tvMaxValueRect)
    }

    private fun formatValue(value: Float): String {
        return if (value != 0f) String.format("%.1f", value) else "0"
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val desiredWidth = 375
        val desiredHeight = bubbleHeight + bubbleMarginBottom + thumbHeight

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)


        //Measure Width
        val width: Int = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                //Must be this size
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                //Can't be bigger than...
                min(desiredWidth, widthSize)
            }
            else -> {
                //Be whatever you want
                desiredWidth
            }
        }


        //Measure Height
        val height: Int = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                //Must be this size
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                //Can't be bigger than...
                min(desiredHeight, heightSize)
            }
            else -> {
                //Be whatever you want
                desiredHeight
            }
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawTextViews(canvas)
        drawBackground(canvas)
        drawProgressShape(canvas)
        drawThumb()
        drawBubbleView()
    }

}