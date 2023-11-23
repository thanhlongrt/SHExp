package com.example.salehub.drawing_on_canvas

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.SweepGradient
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.salehub.R
import kotlin.math.cos
import kotlin.math.sin

class TemperatureView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private lateinit var textViewTemperature: TextView
    private lateinit var textViewHint: TextView
    private val rect: Rect = Rect()
    private val paintBorderBackground = Paint()
    private val paintBorder = Paint()
    private val paintLine = Paint()
    private val paintText = Paint()
    private val paintCircle = Paint()
    private val paintCircleOutline = Paint()
    private val paintPointCurrent = Paint()
    private var colorFrom = Color.parseColor("#FF7373")
    private var colorMid = Color.parseColor("#FF7373")
    private var colorTo = Color.parseColor("#D62020")
    private val colorCircleOutline = Color.parseColor("#33FFFFFF")
    private val colorCircleProgress = Color.parseColor("#CAE2CA")
    private val colortext = Color.BLACK
    private var colorBackgroundBorder = Color.parseColor("#CAE2CA")
    private var gradientColors = intArrayOf(colorFrom, colorTo)
    private var gradientPositions = floatArrayOf(0 / 360f, 360f / 360f)
    private var start = 0f
    private var angle = 0f
    private var oval: RectF = RectF()
    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var radius: Float = 0f
    private val progress = 66f
    private var textSizeTempOnProgressCurve = 0
    private var mListValue = arrayListOf<String>()

    private var _currentAngle = 0f
    private var _newSweepAngle = 0f
    private var _targetSettingAngle = 0f

    private var minValue = 0
    private var maxValue = 0

    private var _state: STATE = STATE.STABLE
    private var _currentTemp: Int? = null
    private var _targetTemp: Int? = _currentTemp
    private var _inProgressSetting = false
    private var _animator: ValueAnimator? = null
    private var _runningState: RUNNING_MODE = RUNNING_MODE.IDLE
    private var strokeWidthBorder = 0f
    private var _padding32dp = 0f
    private var _paddingBottom40dp = 0f
    private var _radiusDotWhite = 0f
    private var _radiusShaderDotWhite = 0f
    private var widthScreen = 0
    private var _progressGradientColors = intArrayOf(
        Color.parseColor("#84CCF5"),
        Color.parseColor("#6AB8C6"), Color.parseColor("#64B22A")
    )
    private var _progressGradientPositions = floatArrayOf(0f / 360f, 180f / 360f, 360f / 360f)

    init {
        widthScreen = Resources.getSystem().displayMetrics.widthPixels
        strokeWidthBorder = context.resources.getDimension(R.dimen._14sp)
        _radiusDotWhite = context.resources.getDimension(R.dimen._14dp)
        _radiusShaderDotWhite = context.resources.getDimension(R.dimen._10dp)
        _padding32dp = RATIO_PADDING_TOP * widthScreen
        _paddingBottom40dp = RATIO_PADDING_BOTTOM * widthScreen
        initColorByState()
        initPaint()
        initText()
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
//        val widthScreen = Resources.getSystem().displayMetrics.widthPixels
//        radius = (RATIO_CIRCLE_PROGRESS * widthScreen) / 2
//        val height = radius * 2 + _paddingBottom40dp

        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        _padding32dp = heightSize * 0.1f
        _paddingBottom40dp = heightSize * 0.1f
        radius = (heightSize - _padding32dp - _paddingBottom40dp) / 2
        strokeWidthBorder = heightSize * 0.06f
        initPaint()
        initShader()
        super.setMeasuredDimension(heightSize, heightSize)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        radius = (RATIO_CIRCLE_PROGRESS * widthScreen) / 2
        val width = width.toFloat()
        val height = height.toFloat()
        paintBorder.style = Paint.Style.STROKE
        paintBorder.isAntiAlias = true
        centerX = width / 2
        centerY = (height - _paddingBottom40dp) / 2 + _padding32dp

        oval.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius)

        val heightOfTextViews = textViewTemperature.height + textViewHint.height
        if (textViewHint.visibility == View.GONE) {
            textViewTemperature.y = (centerY - radius * RATIO_CIRCLE_IN) +
                (radius * RATIO_CIRCLE_IN * 2 - textViewTemperature.height) / 2
        } else {
            textViewTemperature.y = (centerY - radius * RATIO_CIRCLE_IN) +
                (radius * RATIO_CIRCLE_IN * 2 - heightOfTextViews) / 2

            textViewHint.y = textViewTemperature.y + textViewTemperature.height
        }
        canvas?.let {
            drawCircleCenterOut(it)
            drawCircleCenterIn(it)
            drawBorderBackground(it)
            drawNumeral(it)
            if (_runningState == RUNNING_MODE.REDUCE) {
                drawBorder(it)
                drawTextSettingTemp(it)
                drawDotCircle(formatStringTemp(_targetTemp!!), canvas)
            }
            if (_runningState == RUNNING_MODE.INCREASE) {
                drawTextSettingTemp(it)
                drawDotCircle(formatStringTemp(_targetTemp!!), canvas)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        initShader()
        initValues()
    }

    fun getRunningState(): RUNNING_MODE {
        return _runningState
    }

    private fun initPaint() {
        paintCircleOutline.strokeWidth = strokeWidthBorder
        paintCircleOutline.style = Paint.Style.FILL_AND_STROKE
        paintCircleOutline.strokeCap = Paint.Cap.ROUND
        paintCircleOutline.color = colorCircleOutline
        paintCircleOutline.isAntiAlias = true

        paintCircle.strokeWidth = strokeWidthBorder
        paintCircle.style = Paint.Style.FILL_AND_STROKE
        paintCircle.strokeCap = Paint.Cap.ROUND
        paintCircle.color = colorFrom
        paintCircle.isAntiAlias = true

        paintBorder.strokeWidth = strokeWidthBorder
        paintBorder.style = Paint.Style.STROKE
        paintBorder.strokeCap = Paint.Cap.ROUND
        paintBorder.color = colorCircleProgress
        paintBorder.isAntiAlias = true

        paintBorderBackground.strokeWidth = strokeWidthBorder
        paintBorderBackground.color = colorBackgroundBorder // Color.GRAY // default
        paintBorderBackground.style = Paint.Style.STROKE
        paintBorderBackground.strokeCap = Paint.Cap.ROUND
        paintBorderBackground.isAntiAlias = true

        paintLine.strokeWidth = strokeWidthLine
        paintLine.color = colorFrom // default
        paintLine.style = Paint.Style.STROKE
        paintLine.strokeCap = Paint.Cap.ROUND
        paintLine.isAntiAlias = true

        textSizeTempOnProgressCurve = resources.getDimensionPixelSize(R.dimen._14sp)
//        val paintTypeFace = ResourcesCompat.getFont(context, R.font.roboto_medium)
//        paintText.typeface = paintTypeFace
        paintText.strokeWidth = strokeWidthText
        paintText.color = colortext // default
        paintText.style = Paint.Style.FILL
        paintText.isAntiAlias = true
        paintText.textSize = textSizeTempOnProgressCurve.toFloat()

        paintPointCurrent.color = Color.WHITE
        paintPointCurrent.style = Paint.Style.FILL_AND_STROKE
        paintPointCurrent.strokeCap = Paint.Cap.ROUND
        paintPointCurrent.isAntiAlias = true
        val _shadowColor = Color.parseColor("#664F5979")
        paintPointCurrent.setShadowLayer(_radiusShaderDotWhite, 0f, 0f, _shadowColor)
        setLayerType(LAYER_TYPE_NONE, paintPointCurrent)
    }

    private fun initColorByState() {
        when (_state) {
            STATE.STABLE, STATE.UNSTABLE -> {
                colorFrom = Color.parseColor("#84CCF5")
                colorMid = Color.parseColor("#6AB8C6")
                colorTo = Color.parseColor("#64B22A")
                colorBackgroundBorder = Color.parseColor("#CAE2CA")
                gradientColors = intArrayOf(colorFrom, colorMid, colorTo)
                gradientPositions = floatArrayOf(0f / 360f, 180f / 360f, 360f / 360f)
            }
            STATE.WARNING_RED, STATE.STOP_WORKING -> {
                colorFrom = Color.parseColor("#FF7373")
                colorMid = Color.parseColor("#F24648")
                colorTo = Color.parseColor("#D62020")
                colorBackgroundBorder = Color.parseColor("#F9D7D7")
                gradientColors = intArrayOf(colorFrom, colorMid, colorTo)
                gradientPositions = floatArrayOf(0f / 360f, 180f / 360f, 360f / 360f)
            }
            STATE.NOSIGNAL -> {
                colorFrom = Color.parseColor("#E6EBED")
                colorTo = Color.parseColor("#ACBAC2")
                colorBackgroundBorder = Color.parseColor("#DEE5EA")
                gradientColors = intArrayOf(colorFrom, colorTo)
                gradientPositions = floatArrayOf(0f / 360f, 360f / 360f)
            }
            STATE.WARNING_YELLOW -> {
                colorFrom = Color.parseColor("#FFD585")
                colorMid = Color.parseColor("#FFBA34")
                colorTo = Color.parseColor("#FCA600")
                colorBackgroundBorder = Color.parseColor("#F8E8C9")
                gradientColors = intArrayOf(colorFrom, colorMid, colorTo)
                gradientPositions = floatArrayOf(0f / 360f, 180f / 360f, 360f / 360f)
            }
        }
    }

    private fun drawCircleCenterOut(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, radius * 0.75f, paintCircle)
    }

    private fun drawCircleCenterIn(canvas: Canvas) {
        canvas.drawCircle(centerX, centerY, radius * RATIO_CIRCLE_IN, paintCircleOutline)
    }

    private fun drawBorder(canvas: Canvas) {
        canvas.drawArc(oval, _currentAngle, _newSweepAngle, false, paintBorder)
    }

    private fun drawBorderBackground(canvas: Canvas) {
        canvas.drawArc(oval, start, angle, false, paintBorderBackground)
    }

    private fun drawNumeral(canvas: Canvas) {
        mListValue.clear()
        for (i in minValue..maxValue) {
//            mListValue.add("${i.formatTemperature()}°C")
        }
        if (mListValue.isNotEmpty()) {
            drawMaxMinValue(canvas)
        }
    }

    private fun drawTextSettingTemp(canvas: Canvas) {
        drawTextTempSelected(
            temp = _targetTemp!!,
            canvas = canvas,
        )
    }

    private fun drawMaxMinValue(canvas: Canvas) {
        drawTextTempSelected(maxValue, canvas)
        drawTextTempSelected(minValue, canvas)
    }

    private fun drawTextTempSelected(
        temp: Int,
        canvas: Canvas
    ) {
        val item = formatStringTemp(temp)
        val position = mListValue.indexOf(item) // getPosition item
        val total = 240f // 2/3 circle
        val edge = ((total / (mListValue.size - 1))) * (position) // edge pos
        val angle = edge + 150f // angle now
        val radians = angle * (Math.PI / 180) // calc radians
        val radiusX = radius * 1.3
        val radiusY = radius * 1.10
        paintText.getTextBounds(item, 0, item.length, rect)
        val xText = width.toFloat() / 2 + cos(radians) * radiusX - rect.width().toFloat() / 2
        val yTEXT = if (angle in 210f..330f) {
            centerY + sin(radians) * radiusY - rect.height().toFloat() / 2
        } else centerY + sin(radians) * radius + rect.height().toFloat() / 2
        canvas.drawText(item, xText.toFloat(), yTEXT.toFloat(), paintText)
    }

    private fun drawDotCircle(item: String, canvas: Canvas) {
        val position = mListValue.indexOf(item) // getPosition item
        val total = progress * 360 / 100 // 66f == 2/3 circle
        val edge = ((total / (mListValue.size)) * 1.05) * position // edge pos
        val angle = edge + 150f // angle now
        val radians = angle * (Math.PI / 180) // calc radians

        val xCircle = (width / 2 + cos(radians) * radius).toInt()
        val yCircle = (centerY + sin(radians) * radius).toInt()

        canvas.drawCircle(xCircle.toFloat(), yCircle.toFloat(), _radiusDotWhite, paintPointCurrent)
    }

    private fun calculateAngle(temp: Int): Float {
        val values = formatStringTemp(temp)
        val position = mListValue.indexOf(values) // getPosition item
        val total = progress * 360 / 100 // 66f == 2/3 circle
        val edge = ((total / (mListValue.size)) * 1.05) * position // edge pos
        val angle = edge + 150f // angle now
        return angle.toFloat()
    }

    fun setMinMaxProgress(minValue: Int, maxValue: Int) {
        this.minValue = minValue
        this.maxValue = maxValue
        mListValue.clear()
        for (i in minValue..maxValue) {
//            mListValue.add("${i.formatTemperature()}°C")
        }
        invalidate()
    }

    private fun initValues() {
        angle = progress * 360 / 100 // total
        var remain = 360 - angle
        val startAngle = remain / 2
        start = 90f + startAngle
    }

    private fun initShader() {
        if (width <= 0 || height <= 0) return
        val shader =
            SweepGradient(
                width / 2f, height / 2f, _progressGradientColors,
                _progressGradientPositions
            ).apply {
                val rotate = 0f
                val gradientMatrix = Matrix()
                gradientMatrix.preRotate(rotate, width / 2F, height / 2F)
                setLocalMatrix(gradientMatrix)
            }
        paintBorder.shader = shader
        paintLine.shader = shader

        val shaderCircle =
            RadialGradient(
                (width * 0.53).toFloat(), (height * 0.583).toFloat(), (height / 2).toFloat(),
                intArrayOf(
                    ContextCompat.getColor(context, R.color.colorWhite),
                    ContextCompat.getColor(context, R.color.colorWhite),
                    ContextCompat.getColor(context, R.color.colorWhite),
                    ContextCompat.getColor(context, R.color.colorWhite90),
                    ContextCompat.getColor(context, R.color.colorWhite80),
                    ContextCompat.getColor(context, R.color.colorGrey),
                ),
                floatArrayOf(0.0f, 0.2f, 0.4f, 0.5f, 0.55f, 1f),
                Shader.TileMode.CLAMP
            )
        paintCircle.shader = shaderCircle
    }

    @SuppressLint("ResourceType")
    private fun initText() {
        textViewTemperature = TextView(context)
        val layoutParamsTitle = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        val layoutParamsDescription =
            LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        layoutParamsTitle.addRule(CENTER_HORIZONTAL)
        textViewTemperature.layoutParams = layoutParamsTitle
//        val typeface = ResourcesCompat.getFont(context, R.font.roboto_bold)
//        textViewTemperature.typeface = typeface
        textViewTemperature.id = 1
        textViewTemperature.textSize = 52f
        textViewTemperature.includeFontPadding = false
        textViewTemperature.setTextColor(Color.WHITE)
        textViewTemperature.gravity = Gravity.CENTER
        val tempString = if (_currentTemp == null) {
            "--"
        } else formatStringTemp(_currentTemp!!)
        textViewTemperature.text = tempString

        textViewHint = TextView(context)
        layoutParamsDescription.addRule(CENTER_HORIZONTAL)
        layoutParamsDescription.addRule(BELOW, textViewTemperature.id)
        textViewHint.layoutParams = layoutParamsDescription
//        val typefaceTextViewHint = ResourcesCompat.getFont(context, R.font.roboto_medium)
//        textViewHint.typeface = typefaceTextViewHint
        textViewHint.includeFontPadding = false
        removeAllViews()
        textViewHint.textSize = 14f
        textViewHint.setTextColor(Color.WHITE)
        textViewHint.gravity = Gravity.CENTER
//        textViewHint.text = context.resources.getString(R.string.current_temperature)
        addView(textViewTemperature)
        addView(textViewHint)
    }

    fun updateState(state: STATE) {
        if (_state == state) return
        invalidate()
        _state = state
        if (_state == STATE.NOSIGNAL || _state == STATE.STOP_WORKING) {
            if (_state == STATE.NOSIGNAL) {
                textViewHint.visibility = View.GONE
            }
            textViewTemperature.text = "--"
        } else {
            textViewHint.visibility = View.VISIBLE
        }
        initColorByState()
        initPaint()
        initShader()
        initValues()
    }

    fun settingTemp(temp: Int) {
        if (_currentTemp == null) return
        _targetTemp = temp
        _inProgressSetting = true
        _runningState = when {
            _targetTemp == _currentTemp -> RUNNING_MODE.IDLE
            _targetTemp!! > _currentTemp!! -> RUNNING_MODE.INCREASE
            _targetTemp!! < _currentTemp!! -> RUNNING_MODE.REDUCE
            else -> RUNNING_MODE.IDLE
        }
        _currentAngle = if (_currentTemp!! > maxValue) {
            calculateAngle(maxValue)
        } else {
            calculateAngle(_currentTemp!!)
        }
        _targetSettingAngle = calculateAngle(_targetTemp!!)
        _newSweepAngle = -Math.abs(_targetSettingAngle - _currentAngle)
        if (_runningState == RUNNING_MODE.INCREASE) {
            invalidate()
            return
        }
        if (_runningState == RUNNING_MODE.REDUCE) {
            val updateListener: (Float) -> Unit = { animatedValue ->
                _newSweepAngle = animatedValue
                invalidate()
            }
            val endListener: () -> Unit = {
            }
            startAnimation(0f, _newSweepAngle, updateListener, endListener)
        }
    }

    fun setTemp(value: Int) {
        if (!_inProgressSetting) {
            _currentTemp = value
            _currentAngle = calculateAngle(value)
        }
        setTemperatureTitle(formatStringTemp(value))
    }

    private fun startAnimation(
        start: Float,
        end: Float,
        updateListener: (Float) -> Unit,
        endListener: () -> Unit
    ) {
        _animator = ValueAnimator.ofFloat(start, end)
        _animator?.cancel()
        _animator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                endListener.invoke()
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })
        _animator?.addUpdateListener {
            updateListener.invoke(it.animatedValue as Float)
        }
        _animator?.repeatMode = ValueAnimator.REVERSE
        _animator?.repeatCount = ValueAnimator.INFINITE
        _animator?.duration = 1000L
        _animator?.start()
    }

    fun cancelSettingTemp() {
        _runningState = RUNNING_MODE.IDLE
        _inProgressSetting = false
        _animator?.cancel()
        _animator = null
        invalidate()
    }

    fun resetItemInView() {
        _currentAngle = 0f
        _newSweepAngle = 0f
    }

    fun setTemperatureTitle(title: String) {
        textViewTemperature.text = title
    }

    fun formatStringTemp(temp: Int): String {
//        return String.format(
//            context.resources.getString(R.string.temp_format), temp.formatTemperature()
//        )
        return ""
    }

    enum class RUNNING_MODE {
        REDUCE,
        INCREASE,
        IDLE
    }

    enum class STATE {
        STABLE,
        UNSTABLE,
        WARNING_RED,
        WARNING_YELLOW,
        NOSIGNAL,
        STOP_WORKING
    }

    companion object {
        private const val strokeWidthLine = 5f
        private const val strokeWidthText = 3f
        private const val RATIO_CIRCLE_IN = 180f / 237f
        private const val RATIO_CIRCLE_PROGRESS = 237f / 375f
        private const val RATIO_PADDING_TOP = 32f / 375f
        private const val RATIO_PADDING_BOTTOM = 32f / 375f
    }
}
