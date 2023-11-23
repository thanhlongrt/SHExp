package com.example.salehub.drawing_on_canvas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.salehub.R

class FanDetailBottomView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val mStrokeWidth = resources.getDimensionPixelSize(R.dimen._1dp)
    private val mTopCornerRadius = resources.getDimensionPixelSize(R.dimen._20dp).toFloat()
    private val mMiddleCornerRadius = resources.getDimensionPixelSize(R.dimen._18dp).toFloat()
    private val mBottomCornerRadius = resources.getDimensionPixelSize(R.dimen._16dp).toFloat()
    private val mCenterCircleRadius = resources.getDimensionPixelSize(R.dimen._32dp).toFloat()

    private val mPath = Path()
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        strokeWidth = mStrokeWidth.toFloat()
        color = Color.WHITE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (mCenterCircleRectF == null) {
            mCenterCircleRectF = calculateCenterCircleRectF()
        }

        drawPath(canvas)
    }

    private fun calculatePath() {
        mPath.reset()

        val halfWidthWithoutCorners =
            width / 2 - mCenterCircleRadius - mMiddleCornerRadius - mTopCornerRadius
        val heightWithoutCorners =
            height - mTopCornerRadius - mBottomCornerRadius

        val widthWithoutCorners =
            width - mBottomCornerRadius * 2

        mPath.apply {
            moveTo(0f + mTopCornerRadius, 0f)
            rQuadTo(-mTopCornerRadius, 0f, -mTopCornerRadius, mTopCornerRadius)
            rLineTo(0f, heightWithoutCorners)
            rQuadTo(0f, mBottomCornerRadius, mBottomCornerRadius, mBottomCornerRadius)
            rLineTo(widthWithoutCorners, 0f)
            rQuadTo(mBottomCornerRadius, 0f, mBottomCornerRadius, -mBottomCornerRadius)
            rLineTo(0f, -heightWithoutCorners)
            rQuadTo(0f, -mTopCornerRadius, -mTopCornerRadius, -mTopCornerRadius)
            rLineTo(-halfWidthWithoutCorners, 0f)
            rQuadTo(-mMiddleCornerRadius, 0f, -mMiddleCornerRadius, mMiddleCornerRadius)
            addArc(mCenterCircleRectF!!, 0f, 180f)
            rQuadTo(0f, -mMiddleCornerRadius, -mMiddleCornerRadius, -mMiddleCornerRadius)
            rLineTo(-halfWidthWithoutCorners, 0f)
        }
    }

    private fun drawPath(canvas: Canvas?) {
        calculatePath()
        canvas?.drawPath(mPath, mPaint)
    }

    private var mCenterCircleRectF: RectF? = null

    private fun calculateCenterCircleRectF(): RectF {
        val centerX = width / 2
        val centerY = 0f + mMiddleCornerRadius
        return RectF(
            centerX - mCenterCircleRadius,
            centerY - mCenterCircleRadius,
            centerX + mCenterCircleRadius,
            centerY + mCenterCircleRadius
        )
    }
}
