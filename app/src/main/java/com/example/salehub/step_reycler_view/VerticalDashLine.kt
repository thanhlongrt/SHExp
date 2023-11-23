package com.example.salehub.step_reycler_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.salehub.R

class VerticalDashLine : View {

    private val mPaint = Paint()
    private val mPath = Path()
    private var colorDashLine = ContextCompat.getColor(context, R.color.colorLightGrey)

    constructor(context: Context) : super(context) {
        initView(context, attrs = null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initView(context, attrs)
    }

    private fun initView(context: Context, attrs: AttributeSet?) {
//        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.VerticalDashLine, 0, 0)
//        if (typedArray.hasValue(R.styleable.VerticalDashLine_dashColor)) {
//            colorDashLine = typedArray.getColor(R.styleable.VerticalDashLine_dashColor, ContextCompat.getColor(context, R.color.colorLightGrey))
//        }
//        typedArray.recycle()
        mPaint.color = colorDashLine
        mPaint.strokeWidth = context.resources.getDimension(R.dimen._1dp)
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeCap = Paint.Cap.ROUND

        val dash = context.resources.getDimension(R.dimen._5dp)
        val dashEffect = DashPathEffect(floatArrayOf(dash, dash), 0f)
        mPaint.pathEffect = dashEffect
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (h > 0) {
            mPath.reset()
            mPath.moveTo(w / 2f, 0f)
            mPath.lineTo(w / 2f, h.toFloat())
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawPath(mPath, mPaint)
    }
}
