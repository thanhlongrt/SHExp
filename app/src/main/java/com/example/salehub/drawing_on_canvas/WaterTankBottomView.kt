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
import kotlin.math.acos

class WaterTankBottomView : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val mStrokeWidth = resources.getDimensionPixelSize(R.dimen._1dp)
    private val mTopCornerRadius = resources.getDimensionPixelSize(R.dimen._10dp).toFloat()
    private val mMiddleCornerRadius = resources.getDimensionPixelSize(R.dimen._18dp).toFloat()
    private val mBottomCornerRadius = resources.getDimensionPixelSize(R.dimen._16dp).toFloat()
    private val mSlope = resources.getDimensionPixelSize(R.dimen._18dp).toFloat()
    private val mCenterCircleRadius = resources.getDimensionPixelSize(R.dimen._50dp).toFloat()
    private val diff = resources.getDimensionPixelSize(R.dimen._3dp).toFloat()

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
            width / 2 - mCenterCircleRadius - mTopCornerRadius - mMiddleCornerRadius + diff
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
            rLineTo(-halfWidthWithoutCorners, 0f + mSlope)

            rQuadTo(
                -mMiddleCornerRadius + diff,
                0f + diff,
                -mMiddleCornerRadius,
                mMiddleCornerRadius
            )
            val angle = angle(
                (mCenterCircleRadius - diff).toDouble(),
                mCenterCircleRadius.toDouble()
            )
            arcTo(mCenterCircleRectF!!, 0f + angle.toFloat(), 180f - angle.toFloat()*2)
//            addArc(mCenterCircleRectF!!, 0f + angle.toFloat() / 2, 180f - angle.toFloat())

//            moveTo(0f + mTopCornerRadius, 0f)
//            rLineTo(halfWidthWithoutCorners, 0f + mSlope)
//            rQuadTo(
//                0f + mMiddleCornerRadius - diff,
//                0f + diff,
//                0f + mMiddleCornerRadius,
//                0f + mMiddleCornerRadius
//            )

            rQuadTo(
                0f - diff,
                -mMiddleCornerRadius + diff/2,
                -mMiddleCornerRadius,
                -mMiddleCornerRadius
            )
 //            rLineTo(-halfWidthWithoutCorners, 0f - _16dp)
            lineTo(0f + mTopCornerRadius, 0f)
//            close()
//            Log.e(
//                "TAG",
//                "calculatePath: ${
//                angle(
//                    (mCenterCircleRadius - mMiddleCornerRadius / 2).toDouble(),
//                    mCenterCircleRadius.toDouble()
//                )
//                }",
//            )
        }
    }

    private fun angle(ke: Double, huyen: Double): Double {
        return Math.toDegrees(acos(ke / huyen))
    }

    private fun drawPath(canvas: Canvas?) {
        calculatePath()
        canvas?.drawPath(mPath, mPaint)
    }

    private var mCenterCircleRectF: RectF? = null

    private fun calculateCenterCircleRectF(): RectF {
        val centerX = width / 2
        val centerY = 0f + mSlope
        return RectF(
            centerX - mCenterCircleRadius,
            centerY - mCenterCircleRadius,
            centerX + mCenterCircleRadius,
            centerY + mCenterCircleRadius
        )
    }
}
