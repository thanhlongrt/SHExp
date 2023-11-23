package com.example.salehub.tooltip

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.Window
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.salehub.utils.px

class TooltipHelper {
    private var rootView: View? = null
    private val view: View
    private val tooltipView: TooltipView

    private constructor(myContext: MyContext, view: View) {
        this.view = view
        tooltipView = TooltipView(myContext.getContext())
        val scrollParent = findScrollParent(view)
        scrollParent?.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                tooltipView.translationY = tooltipView.translationY - (scrollY - oldScrollY)
            }
        )
    }

    private constructor(myContext: MyContext, rootView: View, view: View) {
        this.rootView = rootView
        this.view = view
        tooltipView = TooltipView(myContext.getContext())
        val scrollParent = findScrollParent(view)
        scrollParent?.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                tooltipView.translationY = tooltipView.translationY - (scrollY - oldScrollY)
            }
        )
    }

    private constructor(view: View) : this(MyContext(getActivityContext(view.context)), view)

    private fun findScrollParent(view: View): NestedScrollView? {
        return if (view.parent == null || view.parent !is View) {
            null
        } else if (view.parent is NestedScrollView) {
            view.parent as NestedScrollView
        } else {
            findScrollParent(view.parent as View)
        }
    }

    fun position(position: Position): TooltipHelper {
        tooltipView.setPosition(position)
        return this
    }

    fun withShadow(withShadow: Boolean): TooltipHelper {
        tooltipView.setWithShadow(withShadow)
        return this
    }

    fun shadowColor(@ColorInt shadowColor: Int): TooltipHelper {
        tooltipView.setShadowColor(shadowColor)
        return this
    }

    fun customView(customView: View): TooltipHelper {
        tooltipView.setCustomView(customView)
        return this
    }

    fun customView(viewId: Int): TooltipHelper {
        tooltipView.setCustomView((view.context as Activity).findViewById(viewId))
        return this
    }

    fun arrowWidth(arrowWidth: Int): TooltipHelper {
        tooltipView.setArrowWidth(arrowWidth)
        return this
    }

    fun arrowHeight(arrowHeight: Int): TooltipHelper {
        tooltipView.setArrowHeight(arrowHeight)
        return this
    }

    fun arrowSourceMargin(arrowSourceMargin: Int): TooltipHelper {
        tooltipView.setArrowSourceMargin(arrowSourceMargin)
        return this
    }

    fun arrowTargetMargin(arrowTargetMargin: Int): TooltipHelper {
        tooltipView.setArrowTargetMargin(arrowTargetMargin)
        return this
    }

    fun align(align: ALIGN?): TooltipHelper {
        tooltipView.setAlign(align)
        return this
    }

    fun show(): TooltipView {
        val activityContext = tooltipView.context
        if (activityContext != null && activityContext is Activity) {
            val decorView =
                if (rootView != null) rootView as ViewGroup else (activityContext.window.decorView as ViewGroup)
            view.postDelayed({
                val rect = Rect()
                view.getGlobalVisibleRect(rect)
                val rootGlobalRect = Rect()
                val rootGlobalOffset = Point()
                decorView.getGlobalVisibleRect(rootGlobalRect, rootGlobalOffset)
                val location = IntArray(2)
                view.getLocationOnScreen(location)
                rect.left = location[0]
                rect.top -= rootGlobalOffset.y
                rect.bottom -= rootGlobalOffset.y
                rect.left -= rootGlobalOffset.x
                rect.right -= rootGlobalOffset.x
                decorView.addView(
                    tooltipView,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                tooltipView.viewTreeObserver.addOnPreDrawListener(object :
                        ViewTreeObserver.OnPreDrawListener {
                        override fun onPreDraw(): Boolean {
                            tooltipView.setup(rect, decorView.width)
                            tooltipView.viewTreeObserver.removeOnPreDrawListener(this)
                            return false
                        }
                    })
            }, 100)
        }
        return tooltipView
    }

    fun close() {
        tooltipView.close()
    }

    fun duration(duration: Long): TooltipHelper {
        tooltipView.setDuration(duration)
        return this
    }

    fun color(color: Int): TooltipHelper {
        tooltipView.setColor(color)
        return this
    }

    fun color(paint: Paint): TooltipHelper {
        tooltipView.setPaint(paint)
        return this
    }

    fun onDisplay(listener: ListenerDisplay?): TooltipHelper {
        tooltipView.setListenerDisplay(listener)
        return this
    }

    fun onHide(listener: ListenerHide?): TooltipHelper {
        tooltipView.setListenerHide(listener)
        return this
    }

    fun padding(left: Int, top: Int, right: Int, bottom: Int): TooltipHelper {
        tooltipView.ttPaddingTop = top
        tooltipView.ttPaddingBottom = bottom
        tooltipView.ttPaddingLeft = left
        tooltipView.ttPaddingRight = right
        return this
    }

    fun animation(tooltipAnimation: TooltipAnimation): TooltipHelper {
        tooltipView.setTooltipAnimation(tooltipAnimation)
        return this
    }

    fun text(text: String?): TooltipHelper {
        tooltipView.setText(text)
        return this
    }

    fun text(@StringRes text: Int): TooltipHelper {
        tooltipView.setText(text)
        return this
    }

    fun corner(corner: Int): TooltipHelper {
        tooltipView.setCorner(corner)
        return this
    }

    fun textColor(textColor: Int): TooltipHelper {
        tooltipView.setTextColor(textColor)
        return this
    }

    fun textTypeFace(typeface: Typeface?): TooltipHelper {
        tooltipView.setTextTypeFace(typeface)
        return this
    }

    fun textSize(unit: Int, textSize: Float): TooltipHelper {
        tooltipView.setTextSize(unit, textSize)
        return this
    }

    fun margin(left: Int, top: Int, right: Int, bottom: Int): TooltipHelper {
        tooltipView.setMargin(left, top, right, bottom)
        return this
    }

    fun setTextGravity(textGravity: Int): TooltipHelper {
        tooltipView.setTextGravity(textGravity)
        return this
    }

    fun clickToHide(clickToHide: Boolean): TooltipHelper {
        tooltipView.setClickToHide(clickToHide)
        return this
    }

    fun autoHide(autoHide: Boolean, duration: Long): TooltipHelper {
        tooltipView.setAutoHide(autoHide)
        tooltipView.setDuration(duration)
        return this
    }

    fun distanceWithView(distance: Int): TooltipHelper {
        tooltipView.setDistanceWithView(distance)
        return this
    }

    fun border(color: Int, width: Float): TooltipHelper {
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        borderPaint.color = color
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = width
        tooltipView.setBorderPaint(borderPaint)
        return this
    }

    enum class Position {
        LEFT, RIGHT, TOP, BOTTOM
    }

    enum class ALIGN {
        START, CENTER, END
    }

    interface TooltipAnimation {
        fun animateEnter(view: View?, animatorListener: Animator.AnimatorListener?)
        fun animateExit(view: View?, animatorListener: Animator.AnimatorListener?)
    }

    interface ListenerDisplay {
        fun onDisplay(view: View?)
    }

    interface ListenerHide {
        fun onHide(view: View?)
    }

    class FadeTooltipAnimation : TooltipAnimation {
        private var fadeDuration: Long = DEFAULT_FADE_DURATION

        constructor()
        constructor(fadeDuration: Long) {
            this.fadeDuration = fadeDuration
        }

        override fun animateEnter(view: View?, animatorListener: Animator.AnimatorListener?) {
            view?.alpha = 0f
            view?.animate()?.alpha(1f)?.setDuration(fadeDuration)?.setListener(animatorListener)
        }

        override fun animateExit(view: View?, animatorListener: Animator.AnimatorListener?) {
            view?.animate()?.alpha(0f)?.setDuration(fadeDuration)?.setListener(animatorListener)
        }

        companion object {
            private const val DEFAULT_FADE_DURATION = 200L
        }
    }

    class TooltipView(context: Context?) : FrameLayout(context!!) {
        private var arrowHeight = 15
        private var arrowWidth = 15
        private var arrowSourceMargin = 0
        private var arrowTargetMargin = 0
        private var childView: View
        private var color = Color.parseColor("#1F7C82")
        private var bubblePath: Path? = null
        private var bubblePaint: Paint
        private var borderPaint: Paint?
        private var position = Position.BOTTOM
        private var align: ALIGN? = ALIGN.CENTER
        private var clickToHide = false
        private var autoHide = true
        private var duration: Long = 4000
        private var listenerDisplay: ListenerDisplay? = null
        private var listenerHide: ListenerHide? = null
        private var tooltipAnimation: TooltipAnimation = FadeTooltipAnimation()
        private var corner = 30
        var ttPaddingTop = 20
        var ttPaddingBottom = 30
        var ttPaddingRight = 30
        var ttPaddingLeft = 30
        private var marginTop = 0
        private var marginBottom = 0
        private var marginRight = 0
        private var marginLeft = 0
        var shadowPadding = 4
        var shadowWidth = 8
        private var viewRect: Rect? = null
        private var distanceWithView = 0
        private var shadowColor = Color.parseColor("#aaaaaa")
        fun setCustomView(customView: View) {
            removeView(childView)
            childView = customView
            addView(
                childView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        fun setColor(color: Int) {
            this.color = color
            bubblePaint.color = color
            postInvalidate()
        }

        fun setShadowColor(color: Int) {
            shadowColor = color
            postInvalidate()
        }

        fun setMargin(left: Int, top: Int, right: Int, bottom: Int) {
            marginLeft = left
            marginTop = top
            marginRight = right
            marginBottom = top
            childView.setPadding(
                childView.paddingLeft + left,
                childView.paddingTop + top,
                childView.paddingRight + right,
                childView.paddingBottom + bottom
            )
            postInvalidate()
        }

        fun setPaint(paint: Paint) {
            bubblePaint = paint
            setLayerType(LAYER_TYPE_SOFTWARE, paint)
            postInvalidate()
        }

        fun setPosition(position: Position) {
            this.position = position
            when (position) {
                Position.TOP -> setPadding(
                    ttPaddingLeft,
                    ttPaddingTop,
                    ttPaddingRight,
                    ttPaddingBottom + arrowHeight
                )
                Position.BOTTOM -> setPadding(
                    ttPaddingLeft,
                    ttPaddingTop + arrowHeight,
                    ttPaddingRight,
                    ttPaddingBottom
                )
                Position.LEFT -> setPadding(
                    ttPaddingLeft,
                    ttPaddingTop,
                    ttPaddingRight + arrowHeight,
                    ttPaddingBottom
                )
                Position.RIGHT -> setPadding(
                    ttPaddingLeft + arrowHeight,
                    ttPaddingTop,
                    ttPaddingRight,
                    ttPaddingBottom
                )
            }
            postInvalidate()
        }

        fun setAlign(align: ALIGN?) {
            this.align = align
            postInvalidate()
        }

        fun setText(text: String?) {
            if (childView is TextView) {
                (childView as TextView).text = Html.fromHtml(text)
            }
            postInvalidate()
        }

        fun setText(text: Int) {
            if (childView is TextView) {
                (childView as TextView).setText(text)
            }
            postInvalidate()
        }

        fun setTextColor(textColor: Int) {
            if (childView is TextView) {
                (childView as TextView).setTextColor(textColor)
            }
            postInvalidate()
        }

        fun getArrowHeight(): Int {
            return arrowHeight
        }

        fun setArrowHeight(arrowHeight: Int) {
            this.arrowHeight = arrowHeight
            postInvalidate()
        }

        fun getArrowWidth(): Int {
            return arrowWidth
        }

        fun setArrowWidth(arrowWidth: Int) {
            this.arrowWidth = arrowWidth
            postInvalidate()
        }

        fun getArrowSourceMargin(): Int {
            return arrowSourceMargin
        }

        fun setArrowSourceMargin(arrowSourceMargin: Int) {
            this.arrowSourceMargin = arrowSourceMargin
            postInvalidate()
        }

        fun getArrowTargetMargin(): Int {
            return arrowTargetMargin
        }

        fun setArrowTargetMargin(arrowTargetMargin: Int) {
            this.arrowTargetMargin = arrowTargetMargin
            postInvalidate()
        }

        fun setTextTypeFace(textTypeFace: Typeface?) {
            if (childView is TextView) {
                (childView as TextView).typeface = textTypeFace
            }
            postInvalidate()
        }

        fun setTextSize(unit: Int, size: Float) {
            if (childView is TextView) {
                (childView as TextView).setTextSize(unit, size)
            }
            postInvalidate()
        }

        fun setTextGravity(textGravity: Int) {
            if (childView is TextView) {
                (childView as TextView).gravity = textGravity
            }
            postInvalidate()
        }

        fun setClickToHide(clickToHide: Boolean) {
            this.clickToHide = clickToHide
        }

        fun setCorner(corner: Int) {
            this.corner = corner
        }

        override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(width, height, oldw, oldh)
            bubblePath = drawBubble(
                RectF(
                    shadowPadding.toFloat(),
                    shadowPadding.toFloat(),
                    (width - shadowPadding * 2).toFloat(),
                    (height - shadowPadding * 2).toFloat()
                ),
                corner.toFloat(), corner.toFloat(), corner.toFloat(), corner.toFloat()
            )
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)
            if (bubblePath != null) {
                canvas.drawPath(bubblePath!!, bubblePaint)
                if (borderPaint != null) {
                    canvas.drawPath(bubblePath!!, borderPaint!!)
                }
            }
        }

        fun setListenerDisplay(listener: ListenerDisplay?) {
            listenerDisplay = listener
        }

        fun setListenerHide(listener: ListenerHide?) {
            listenerHide = listener
        }

        fun setTooltipAnimation(tooltipAnimation: TooltipAnimation) {
            this.tooltipAnimation = tooltipAnimation
        }

        private fun startEnterAnimation() {
            tooltipAnimation.animateEnter(
                this,
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        if (listenerDisplay != null) {
                            listenerDisplay!!.onDisplay(this@TooltipView)
                        }
                    }
                }
            )
        }

        private fun startExitAnimation(animatorListener: Animator.AnimatorListener) {
            tooltipAnimation.animateExit(
                this,
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        animatorListener.onAnimationEnd(animation)
                        if (listenerHide != null) {
                            listenerHide!!.onHide(this@TooltipView)
                        }
                    }
                }
            )
        }

        private fun handleAutoRemove() {
            if (clickToHide) {
                setOnClickListener {
                    if (clickToHide) {
                        remove()
                    }
                }
            }
            if (autoHide) {
                postDelayed({ remove() }, duration)
            }
        }

        fun remove() {
            startExitAnimation(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    removeNow()
                }
            })
        }

        fun setDuration(duration: Long) {
            this.duration = duration
        }

        fun setAutoHide(autoHide: Boolean) {
            this.autoHide = autoHide
        }

        fun setupPosition(rect: Rect) {
            val x: Int
            val y: Int
            if (position == Position.LEFT || position == Position.RIGHT) {
                x = if (position == Position.LEFT) {
                    rect.left - width - distanceWithView
                } else {
                    rect.right + distanceWithView
                }
                y = rect.top + getAlignOffset(height, rect.height())
            } else {
                y = if (position == Position.BOTTOM) {
                    rect.bottom + distanceWithView
                } else { // top
                    rect.top - height - distanceWithView
                }
                x = rect.left + getAlignOffset(width, rect.width())
            }
            translationX = x.toFloat()
            translationY = y.toFloat()
        }

        private fun getAlignOffset(myLength: Int, hisLength: Int): Int {
            return when (align) {
                ALIGN.END -> hisLength - myLength
                ALIGN.CENTER -> (hisLength - myLength) / 2
                else -> 0
            }
        }

        private fun drawBubble(
            myRect: RectF,
            topLeftDiameter: Float,
            topRightDiameter: Float,
            bottomRightDiameter: Float,
            bottomLeftDiameter: Float
        ): Path {
            var topLeftDiameter = topLeftDiameter
            var topRightDiameter = topRightDiameter
            var bottomRightDiameter = bottomRightDiameter
            var bottomLeftDiameter = bottomLeftDiameter
            val path = Path()
            if (viewRect == null) return path
            topLeftDiameter = if (topLeftDiameter < 0) 0f else topLeftDiameter
            topRightDiameter = if (topRightDiameter < 0) 0f else topRightDiameter
            bottomLeftDiameter = if (bottomLeftDiameter < 0) 0f else bottomLeftDiameter
            bottomRightDiameter = if (bottomRightDiameter < 0) 0f else bottomRightDiameter
            val spacingLeft =
                if (position == Position.RIGHT) arrowHeight.toFloat() else marginLeft.toFloat()
            val spacingTop =
                if (position == Position.BOTTOM) arrowHeight.toFloat() else marginTop.toFloat()
            val spacingRight =
                if (position == Position.LEFT) arrowHeight.toFloat() else marginRight.toFloat()
            val spacingBottom =
                if (position == Position.TOP) arrowHeight.toFloat() else marginBottom.toFloat()
            val left = spacingLeft + myRect.left
            val top = spacingTop + myRect.top
            val right = myRect.right - spacingRight
            val bottom = myRect.bottom - spacingBottom - (bubblePaint?.strokeWidth ?: 0f) / 2f
            val centerX = viewRect!!.centerX() - x
            val arrowSourceX = if (listOf(Position.TOP, Position.BOTTOM).contains(position))
                centerX + arrowSourceMargin else centerX
            val arrowTargetX = if (listOf(Position.TOP, Position.BOTTOM).contains(position))
                centerX + arrowTargetMargin else centerX
            val arrowSourceY = if (listOf(Position.RIGHT, Position.LEFT).contains(position))
                bottom / 2f - arrowSourceMargin else bottom / 2f
            val arrowTargetY = if (listOf(Position.RIGHT, Position.LEFT).contains(position))
                bottom / 2f - arrowTargetMargin else bottom / 2f
            path.moveTo(left + topLeftDiameter / 2f, top)
            // LEFT, TOP
            if (position == Position.BOTTOM) {
                path.lineTo(arrowSourceX - arrowWidth, top)
                path.lineTo(arrowTargetX, myRect.top)
                path.lineTo(arrowSourceX + arrowWidth, top)
            }
            path.lineTo(right - topRightDiameter / 2f, top)
            path.quadTo(right, top, right, top + topRightDiameter / 2)
            // RIGHT, TOP
            if (position == Position.LEFT) {
                path.lineTo(right, arrowSourceY - arrowWidth)
                path.lineTo(myRect.right, arrowTargetY)
                path.lineTo(right, arrowSourceY + arrowWidth)
            }
            path.lineTo(right, bottom - bottomRightDiameter / 2)
            path.quadTo(right, bottom, right - bottomRightDiameter / 2, bottom)
            // RIGHT, BOTTOM
            if (position == Position.TOP) {
                path.lineTo(arrowSourceX + arrowWidth, bottom)
                path.lineTo(arrowTargetX, myRect.bottom)
                path.lineTo(arrowSourceX - arrowWidth, bottom)
            }
            path.lineTo(left + bottomLeftDiameter / 2, bottom)
            path.quadTo(left, bottom, left, bottom - bottomLeftDiameter / 2)
            // LEFT, BOTTOM
            if (position == Position.RIGHT) {
                path.lineTo(left, arrowSourceY + arrowWidth)
                path.lineTo(myRect.left, arrowTargetY)
                path.lineTo(left, arrowSourceY - arrowWidth)
            }
            path.lineTo(left, top + topLeftDiameter / 2)
            path.quadTo(left, top, left + topLeftDiameter / 2, top)
            path.close()
            return path
        }

        fun adjustSize(rect: Rect, screenWidth: Int): Boolean {
            val r = Rect()
            getGlobalVisibleRect(r)
            var changed = false
            val layoutParams = layoutParams
            if (position == Position.LEFT && width > rect.left) {
                layoutParams.width = rect.left - MARGIN_SCREEN_BORDER_TOOLTIP - distanceWithView
                changed = true
            } else if (position == Position.RIGHT && rect.right + width > screenWidth) {
                layoutParams.width =
                    screenWidth - rect.right - MARGIN_SCREEN_BORDER_TOOLTIP - distanceWithView
                changed = true
            } else if (position == Position.TOP || position == Position.BOTTOM) {
                var adjustedLeft = rect.left
                var adjustedRight = rect.right
                if (rect.centerX() + width / 2f > screenWidth) {
                    val diff = rect.centerX() + width / 2f - screenWidth
                    adjustedLeft -= diff.toInt()
                    adjustedRight -= diff.toInt()
                    setAlign(ALIGN.CENTER)
                    changed = true
                } else if (rect.centerX() - width / 2f < 0) {
                    val diff = -(rect.centerX() - width / 2f)
                    adjustedLeft += diff.toInt()
                    adjustedRight += diff.toInt()
                    setAlign(ALIGN.CENTER)
                    changed = true
                }
                if (adjustedLeft < 0) {
                    adjustedLeft = 0
                }
                if (adjustedRight > screenWidth) {
                    adjustedRight = screenWidth
                }
                rect.left = adjustedLeft
                rect.right = adjustedRight
            }
            setLayoutParams(layoutParams)
            postInvalidate()
            return changed
        }

        private fun onSetup(myRect: Rect) {
            setupPosition(myRect)
            bubblePath = drawBubble(
                RectF(
                    shadowPadding.toFloat(),
                    shadowPadding.toFloat(), width - shadowPadding * 2f, height - shadowPadding * 2f
                ),
                corner.toFloat(), corner.toFloat(), corner.toFloat(), corner.toFloat()
            )
            startEnterAnimation()
            handleAutoRemove()
        }

        fun setup(viewRect: Rect?, screenWidth: Int) {
            this.viewRect = Rect(viewRect)
            val myRect = Rect(viewRect)
            val changed = adjustSize(myRect, screenWidth)
            if (!changed) {
                onSetup(myRect)
            } else {
                viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                    override fun onPreDraw(): Boolean {
                        onSetup(myRect)
                        viewTreeObserver.removeOnPreDrawListener(this)
                        return false
                    }
                })
            }
        }

        fun close() {
            remove()
        }

        fun removeNow() {
            if (parent != null) {
                val parent = parent as ViewGroup
                parent.removeView(this@TooltipView)
            }
        }

        fun closeNow() {
            removeNow()
        }

        fun setWithShadow(withShadow: Boolean) {
            if (withShadow) {
                shadowPadding = 4
                bubblePaint.setShadowLayer(shadowWidth.toFloat(), 0f, 0f, shadowColor)
            } else {
                shadowPadding = 0
                bubblePaint.setShadowLayer(0f, 0f, 0f, Color.TRANSPARENT)
            }
        }

        fun setDistanceWithView(distanceWithView: Int) {
            this.distanceWithView = distanceWithView
        }

        fun setBorderPaint(borderPaint: Paint?) {
            this.borderPaint = borderPaint
            postInvalidate()
        }

        companion object {
            private const val MARGIN_SCREEN_BORDER_TOOLTIP = 30
        }

        init {
            setWillNotDraw(false)
            childView = TextView(context)
            (childView as TextView).setTextColor(Color.WHITE)
            addView(
                childView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            childView.setPadding(0, 0, 0, 0)
            bubblePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            bubblePaint.color = color
            bubblePaint.style = Paint.Style.STROKE
            bubblePaint.strokeWidth = 1f.px
            borderPaint = null
            setLayerType(LAYER_TYPE_SOFTWARE, bubblePaint)
            setWithShadow(true)
        }
    }

    class MyContext {
        private var fragment: Fragment? = null
        private var context: Context? = null
        private var activity: Activity? = null

        constructor(activity: Activity?) {
            this.activity = activity
        }

        constructor(fragment: Fragment?) {
            this.fragment = fragment
        }

        constructor(context: Context?) {
            this.context = context
        }

        fun getContext(): Context? {
            return if (activity != null) {
                activity
            } else {
                fragment?.activity
            }
        }

        fun getActivity(): Activity? {
            return if (activity != null) {
                activity
            } else {
                fragment?.activity
            }
        }

        val window: Window?
            get() = if (activity != null) {
                activity?.window
            } else {
                (fragment as? DialogFragment)?.let { it.dialog?.window }
                    ?: fragment?.activity?.window
            }
    }

    companion object {
        fun on(view: View): TooltipHelper {
            return TooltipHelper(MyContext(getActivityContext(view.context)), view)
        }

        fun on(fragment: Fragment?, view: View): TooltipHelper {
            return TooltipHelper(MyContext(fragment), view)
        }

        fun on(activity: Activity, view: View): TooltipHelper {
            return TooltipHelper(MyContext(getActivityContext(activity)), view)
        }

        fun on(activity: Activity, rootView: View, view: View): TooltipHelper {
            return TooltipHelper(MyContext(getActivityContext(activity)), rootView, view)
        }

        private fun getActivityContext(context: Context): Activity? {
            var activityContext: Context? = context
            while (activityContext is ContextWrapper) {
                if (activityContext is Activity) {
                    return activityContext
                }
                activityContext = activityContext.baseContext
            }
            return null
        }
    }
}
