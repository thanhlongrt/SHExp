package com.example.salehub.drawing_on_canvas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlurMaskFilter
import android.graphics.BlurMaskFilter.Blur
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 * This custom layout paints a drop shadow behind all children. The size and opacity
 * of the drop shadow is determined by a "depth" factor that can be set and animated.
 */
class ShadowLayout : RelativeLayout {
    var mShadowPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    var mShadowDepth = 0f
    var mShadowBitmap: Bitmap? = null

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context?) : super(context) {
        init()
    }

    /**
     * Called by the constructors - sets up the drawing parameters for the drop shadow.
     */
    private fun init() {
        mShadowPaint.color = Color.BLACK
        mShadowPaint.style = Paint.Style.FILL
        setWillNotDraw(false)
        mShadowBitmap = Bitmap.createBitmap(
            sShadowRect.width(),
            sShadowRect.height(), Bitmap.Config.ARGB_8888
        )
        val c = Canvas(mShadowBitmap!!)
        mShadowPaint.maskFilter = BlurMaskFilter(BLUR_RADIUS.toFloat(), Blur.NORMAL)
        c.translate(BLUR_RADIUS.toFloat(), BLUR_RADIUS.toFloat())
        c.drawRoundRect(
            sShadowRectF, sShadowRectF.width() / 40,
            sShadowRectF.height() / 40, mShadowPaint
        )
    }

    /**
     * The "depth" factor determines the offset distance and opacity of the shadow (shadows that
     * are further away from the source are offset greater and are more translucent).
     * @param depth
     */
    fun setShadowDepth(depth: Float) {
        if (depth != mShadowDepth) {
            mShadowDepth = depth
            mShadowPaint.alpha = (100 + 150 * (1 - mShadowDepth)).toInt()
            invalidate() // We need to redraw when the shadow attributes change
        }
    }

    /**
     * Overriding onDraw allows us to draw shadows behind every child of this container.
     * onDraw() is called to draw a layout's content before the children are drawn, so the
     * shadows will be drawn first, behind the children (which is what we want).
     */
    override fun onDraw(canvas: Canvas) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (child.visibility != VISIBLE || child.alpha == 0f) {
                continue
            }
            val depthFactor = (80 * mShadowDepth).toInt()
            canvas.save()
            canvas.translate(
                (child.left + depthFactor).toFloat(),
                (
                    child.top + depthFactor
                    ).toFloat()
            )
            canvas.concat(child.matrix)
            tempShadowRectF.right = child.width.toFloat()
            tempShadowRectF.bottom = child.height.toFloat()
            canvas.drawBitmap(mShadowBitmap!!, sShadowRect, tempShadowRectF, mShadowPaint)
            canvas.restore()
        }
    }

    companion object {
        const val BLUR_RADIUS = 6
        val sShadowRectF = RectF(0f, 0f, 200f, 200f)
        val sShadowRect = Rect(0, 0, 200 + 2 * BLUR_RADIUS, 200 + 2 * BLUR_RADIUS)
        var tempShadowRectF = RectF(0f, 0f, 0f, 0f)
    }
}
