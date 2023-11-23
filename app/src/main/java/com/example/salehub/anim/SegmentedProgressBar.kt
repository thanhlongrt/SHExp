package com.example.salehub.anim

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.annotation.FloatRange
import androidx.core.view.doOnLayout

class SegmentedProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs) {

    var segmentColor: Int = Color.BLACK
    var segmentAlpha: Float = 1f
    var segmentCount: Int = 3
    var spacing: Float = 4f

    var progressColor: Int = Color.GREEN
    var progressAlpha: Float = 1f
    var progress: Int = 2

    private val segmentPaths = mutableListOf<Path>()
    private val segmentPaints = mutableListOf<Paint>()
    private val segmentCoordinatesComputer = SegmentCoordinatesComputer()

    private val progressPath: Path = Path()
    private val progressPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    var progressDuration: Long = 300L
    var progressInterpolator = LinearInterpolator()

    private var animatedProgressSegmentCoordinates: SegmentCoordinates? = null

    init {
        initSegments()
    }

    private fun initSegments() {
        (0 until segmentCount).forEach { _ ->
            segmentPaths.add(Path())
            segmentPaints.add(Paint(Paint.ANTI_ALIAS_FLAG))
        }
    }

    override fun onDraw(canvas: Canvas?) {
        val w = width.toFloat()
        val h = height.toFloat()

        (0 until segmentCount).forEach { position ->
            val path = segmentPaths[position]
            val paint = segmentPaints[position]
            val segmentCoordinates =
                segmentCoordinatesComputer.segmentCoordinates(position, segmentCount, w, spacing)

            drawSegment(canvas, path, paint, segmentCoordinates, segmentColor, segmentAlpha)
        }

        val progressCoordinates =
            segmentCoordinatesComputer.progressCoordinates(progress, segmentCount, w, spacing)
        drawSegment(
            canvas,
            progressPath,
            progressPaint,
            progressCoordinates,
            progressColor,
            progressAlpha
        )

        animatedProgressSegmentCoordinates?.let {
            drawSegment(
                canvas,
                progressPath,
                progressPaint,
                it,
                progressColor,
                progressAlpha
            )
        }
    }

    private fun drawSegment(
        canvas: Canvas?,
        path: Path,
        paint: Paint,
        coordinates: SegmentCoordinates,
        color: Int,
        alpha: Float
    ) {

        path.run {
            reset()
            moveTo(coordinates.topLeftX, 0f)
            lineTo(coordinates.topRightX, 0f)
            lineTo(coordinates.bottomRightX, height.toFloat())
            lineTo(coordinates.bottomLeftX, height.toFloat())
            close()
        }

        paint.color = color
        paint.alpha = alpha.toAlphaPaint()

        canvas?.drawPath(path, paint)
    }

    private fun Float.toAlphaPaint(): Int = (this * 255).toInt()

    fun setProgress(progress: Int, animated: Boolean = false) {
        doOnLayout {
            val newProgressCoordinates =
                segmentCoordinatesComputer.progressCoordinates(
                    progress,
                    segmentCount,
                    width.toFloat(),
                    spacing,
                )

            if (animated) {
                val oldProgressCoordinates =
                    segmentCoordinatesComputer.progressCoordinates(
                        this.progress,
                        segmentCount,
                        width.toFloat(),
                        spacing
                    )

                ValueAnimator.ofFloat(0f, 1f)
                    .apply {
                        duration = progressDuration
                        interpolator = progressInterpolator
                        addUpdateListener {
                            val animationProgress = it.animatedValue as Float
                            val topRightXDiff = oldProgressCoordinates.topRightX.lerp(
                                newProgressCoordinates.topRightX,
                                animationProgress
                            )
                            val bottomRightXDiff = oldProgressCoordinates.bottomRightX.lerp(
                                newProgressCoordinates.bottomRightX,
                                animationProgress
                            )
                            animatedProgressSegmentCoordinates =
                                SegmentCoordinates(0f, topRightXDiff, 0f, bottomRightXDiff)
                            invalidate()
                        }
                        start()
                    }
            } else {
                animatedProgressSegmentCoordinates = SegmentCoordinates(
                    0f,
                    newProgressCoordinates.topRightX,
                    0f,
                    newProgressCoordinates.bottomRightX
                )
                invalidate()
            }

            this.progress = progress.coerceIn(0, segmentCount)
        }
    }

    private fun Float.lerp(
        end: Float,
        @FloatRange(from = 0.0, to = 1.0) amount: Float
    ): Float =
        this * (1 - amount.coerceIn(0f, 1f)) + end * amount.coerceIn(0f, 1f)
}