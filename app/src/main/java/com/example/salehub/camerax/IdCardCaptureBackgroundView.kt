package com.example.salehub.camerax

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.salehub.R

class IdCardCaptureBackgroundView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val backgroundPaint = Paint()

    init {
        setWillNotDraw(false)
        setLayerType(LAYER_TYPE_SOFTWARE, null)
        backgroundPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null) return
        drawTopLeft(canvas)
        drawTopRight(canvas)
        drawBottomRight(canvas)
        drawBottomLeft(canvas)
    }

    private fun drawBottomLeft(canvas: Canvas) {
        val bottomLeft = ContextCompat.getDrawable(context, R.drawable.bottom_left)
        bottomLeft?.setBounds(
            0,
            this@IdCardCaptureBackgroundView.height - bottomLeft.intrinsicHeight,
            bottomLeft.intrinsicWidth,
            this@IdCardCaptureBackgroundView.height
        )
        bottomLeft?.draw(canvas)
    }

    private fun drawBottomRight(canvas: Canvas) {
        val bottomRight = ContextCompat.getDrawable(context, R.drawable.bottom_right)
        bottomRight?.setBounds(
            this@IdCardCaptureBackgroundView.width - bottomRight.intrinsicWidth,
            this@IdCardCaptureBackgroundView.height - bottomRight.intrinsicHeight,
            this@IdCardCaptureBackgroundView.width,
            this@IdCardCaptureBackgroundView.height
        )
        bottomRight?.draw(canvas)
    }

    private fun drawTopRight(canvas: Canvas) {
        val topRight = ContextCompat.getDrawable(context, R.drawable.top_right)
        topRight?.setBounds(
            this@IdCardCaptureBackgroundView.width - topRight.intrinsicWidth,
            0,
            this@IdCardCaptureBackgroundView.width,
            topRight.intrinsicHeight
        )
        topRight?.draw(canvas)
    }

    private fun drawTopLeft(canvas: Canvas) {
        val topLeft = ContextCompat.getDrawable(context, R.drawable.top_left)
        topLeft?.setBounds(
            0, 0, topLeft.intrinsicWidth, topLeft.intrinsicHeight
        )
        topLeft?.draw(canvas)
    }
}
