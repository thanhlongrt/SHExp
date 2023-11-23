package com.example.salehub.otp_edit_text

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.example.salehub.R
import com.example.salehub.utils.px
import kotlin.math.roundToInt

class OtpEditText : AppCompatEditText {
    private var space = 11f.px
    private var maxLength = 6
    private var borderWidth = 1f.px
    private var borderWidthActive = 1.5f.px
    private var radius = 8f.px
    private var borderPaint: Paint? = null
    private var borderPaintActive: Paint? = null
    private var cursorPaint: Paint? = null
    private var cursorWidth = 1f.px
    private var cursorVerticalMargin = 10f.px
    private var clickListener: OnClickListener? = null
    private var blinkOn = false
    private var needShowCursor = false
    private var textWidths: FloatArray? = null
    private val textRect = Rect()
    private var charContainerSize = 0f
    private var borderColor: Int = 0
    private var borderColorActive: Int = 0
    private var borderColorError: Int = 0

    var state: State = State.Normal
        set(value) {
            field = value
            initPaints()
            invalidate()
        }

    var onFullFillOtp: ((String) -> Unit)? = null
        set(value) {
            field = value
            doOnTextChanged { text, _, _, _ ->
                if (text?.length == maxLength) {
                    value?.invoke(text.toString())
                }
            }
        }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.OtpEditText, 0, 0).run {
            try {
                maxLength = getInteger(R.styleable.OtpEditText_android_maxLength, 0)
                textWidths = FloatArray(maxLength)
                needShowCursor = getBoolean(R.styleable.OtpEditText_need_cursor, false)
                borderColor = getColor(
                    R.styleable.OtpEditText_border_color,
                    ContextCompat.getColor(context, R.color.neutral_1)
                )
                borderColorActive = getColor(
                    R.styleable.OtpEditText_border_color_active,
                    ContextCompat.getColor(context, R.color.primary_100)
                )
                borderColorError = getColor(
                    R.styleable.OtpEditText_border_color_error,
                    ContextCompat.getColor(context, R.color.other_error)
                )
            } finally {
                recycle()
            }
        }
        initPaints()
        setBackgroundResource(0)
        super.setOnClickListener { v -> // When tapped, move cursor to end of text.
            setSelection(text!!.length)
            clickListener?.onClick(v)
        }
        blinking()
    }

    private fun initPaints() {
        borderPaint = Paint(paint).apply {
            style = Paint.Style.STROKE
            when (state) {
                State.Normal -> {
                    color = borderColor
                    strokeWidth = borderWidth
                }
                State.Error -> {
                    color = borderColorError
                    strokeWidth = borderWidthActive
                }
            }
        }

        borderPaintActive = Paint(borderPaint).apply {
            color = when (state) {
                State.Normal -> {
                    borderColorActive
                }
                State.Error -> {
                    borderColorError
                }
            }
            strokeWidth = borderWidthActive
        }

        cursorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            strokeWidth = cursorWidth
            style = Paint.Style.FILL
            color = Color.BLACK
        }
    }

    override fun setOnClickListener(l: OnClickListener?) {
        clickListener = l
    }

//    override fun setCustomSelectionActionModeCallback(actionModeCallback: ActionMode.Callback) {
//        throw RuntimeException("setCustomSelectionActionModeCallback() not supported.")
//    }

    fun blinking() {
        if (!needShowCursor) return
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            blinkOn = !blinkOn
            invalidate()
            blinking()
        }, 500)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = MeasureSpec.getSize(widthMeasureSpec)
        charContainerSize = (width - space * (maxLength - 1)) / maxLength

        setMeasuredDimension(width, charContainerSize.roundToInt())
    }

    override fun onDraw(canvas: Canvas?) {
        var startX = borderWidthActive / 2
        val bottom = height

        // Text Width
        val text = text ?: ""
        val textLength = text.length
        paint.getTextWidths(getText(), 0, textLength, textWidths)
        paint.getTextBounds(text.toString(), 0, text.length, textRect)
        var i = 0
        while (i < maxLength) {
            if (needShowCursor && text.length == i && blinkOn && isFocused) {
                drawCursor(canvas, startX, charContainerSize, bottom)
            }

            if (text.length > i) {
                val middle = startX + charContainerSize / 2
                canvas?.drawText(
                    text,
                    i,
                    i + 1,
                    middle - (textWidths?.getOrNull(0) ?: 0f) / 2,
                    bottom / 2f + textRect.height() / 2f,
                    paint
                )
                canvas?.drawRoundRect(
                    startX,
                    1f.px,
                    startX + charContainerSize,
                    bottom.toFloat() - 1f.px,
                    radius,
                    radius,
                    borderPaintActive!!
                )
            } else {
                canvas?.drawRoundRect(
                    startX,
                    1f.px,
                    startX + charContainerSize,
                    bottom.toFloat() - 1f.px,
                    radius,
                    radius,
                    borderPaint!!
                )
            }
            startX += if (space < 0) {
                (charContainerSize * 2).toInt()
            } else {
                (charContainerSize + space).toInt()
            }
            i++
        }
    }

    private fun drawCursor(
        canvas: Canvas?,
        startX: Float,
        charSize: Float,
        bottom: Int
    ) {
        canvas?.drawLine(
            startX + charSize / 2,
            cursorVerticalMargin,
            startX + charSize / 2,
            bottom.toFloat() - cursorVerticalMargin,
            cursorPaint!!
        )
    }

    enum class State {
        Normal, Error
    }
}
