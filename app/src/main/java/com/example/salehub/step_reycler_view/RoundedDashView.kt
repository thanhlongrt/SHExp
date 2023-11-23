package com.example.salehub.step_reycler_view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.example.salehub.R


class RoundedDashView : View {
    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val path: Path = Path()
    private var gap = 6
    private var dashWidth = 8
    private var orientation = HORIZONTAL
    private var color: Int = ContextCompat.getColor(context, R.color.colorLightGrey)

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val a: TypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.RoundedDashView,
            defStyleAttr,
            R.style.RoundedDasViewDefault
        )
        orientation = a.getInt(R.styleable.RoundedDashView_orientation, VERTICAL)
        color = a.getColor(R.styleable.RoundedDashView_dividerDashColor, color)
        gap = a.getDimensionPixelSize(R.styleable.RoundedDashView_dividerDashGap, gap)
        dashWidth = a.getDimensionPixelSize(R.styleable.RoundedDashView_dividerDashWidth, dashWidth)
        init()
        a.recycle()
    }

    fun init() {
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = context.resources.getDimension(R.dimen._2dp)
        paint.color = color
        paint.pathEffect = DashPathEffect(
            floatArrayOf(dashWidth.toFloat(), gap.toFloat()),
            0f
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        path.reset()
        applyOrientation()
        canvas.drawPath(path, paint)
    }

    fun setOrientation(orientation: Int) {
        this.orientation = orientation
        applyOrientation()
        invalidate()
    }

    fun applyOrientation() {
        if (orientation == VERTICAL) {
            path.moveTo(width.toFloat() / 2, 0f + dashWidth)
            path.lineTo(
                width.toFloat() / 2, height.toFloat()
            )
        } else if (orientation == HORIZONTAL) {
            path.moveTo(0f + dashWidth, height.toFloat() / 2)
            path.lineTo(
                width.toFloat(), height.toFloat() / 2
            )
        }
    }

    fun setColor(color: Int) {
        this.color = color
        paint.color = color
        invalidate()
    }

    fun getColor(): Int {
        return color
    }

    companion object {
        private const val HORIZONTAL = 0
        private const val VERTICAL = 1
    }
}