package com.example.salehub.slide_bar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.salehub.R

class BubbleView : View {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    var strokeWidth = resources.getDimensionPixelOffset(R.dimen._1dp).toFloat()

    var textSize = resources.getDimensionPixelSize(R.dimen._20dp).toFloat()


    var outlinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = ContextCompat.getColor(context, R.color.colorGrey)
        this.strokeWidth = this@BubbleView.strokeWidth
        strokeJoin = Paint.Join.ROUND
    }

    var whitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.WHITE
        this.strokeWidth = this@BubbleView.strokeWidth
    }

    var textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context, R.color.colorBlackText)
        textSize = this@BubbleView.textSize
        textAlign = Paint.Align.CENTER
    }

    var path = Path()

    var radius = resources.getDimensionPixelOffset(R.dimen._6dp).toFloat()

    var content: String? = "null"


    private fun drawBubble(canvas: Canvas?) {
        val rectF = RectF().apply {
            top = 0f + strokeWidth
            bottom = height.toFloat() - width.toFloat() / 8
            left = 0f + strokeWidth
            right = width.toFloat() - strokeWidth
        }
        canvas?.drawRoundRect(rectF, radius, radius, outlinePaint)

        canvas?.drawLine(
            width.toFloat() / 2 - width / 8, height - width.toFloat() / 8,
            width.toFloat() / 2 + width / 8, height - width.toFloat() / 8,
            whitePaint
        )

        path.moveTo(width.toFloat() / 2 - width / 8, height - width.toFloat() / 8)
        path.lineTo(width.toFloat() / 2, height.toFloat())
        path.lineTo(width.toFloat() / 2 + width / 8, height - width.toFloat() / 8)

        canvas?.drawPath(path, outlinePaint)

        content?.let {
            canvas?.drawText(
                it,
                width.toFloat() / 2,
                height.toFloat() / 2 + width.toFloat() / 8,
                textPaint
            )
        }
    }

    fun setContentString(content: String) {
        this.content = content
        invalidate()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBubble(canvas)
    }
}