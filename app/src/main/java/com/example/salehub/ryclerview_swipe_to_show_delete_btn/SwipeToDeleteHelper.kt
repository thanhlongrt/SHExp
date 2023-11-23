package com.canon.cebm.miniprint.android.us.scenes.menu.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.salehub.R
import java.util.LinkedList
import java.util.Queue

abstract class SwipeToDeleteHelper(context: Context?) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {


    companion object {
        const val BUTTON_WIDTH = 200
    }

    private var recyclerView: RecyclerView? = null
    private var buttons: MutableList<UnderlayButton> = mutableListOf()
    private val gestureDetector: GestureDetector
    private var swipedPos = -1
    private var swipeThreshold = 0.5f
    private val buttonsBuffer: MutableMap<Int, MutableList<UnderlayButton>>
    private val recoverQueue: Queue<Int>


    private val gestureListener: GestureDetector.SimpleOnGestureListener =
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                for (button in buttons) {
                    if (button.onClick(e.x, e.y)) break
                }
                return true
            }
        }

    init {
        gestureDetector = GestureDetector(context, gestureListener)
        buttonsBuffer = HashMap()
        recoverQueue = object : LinkedList<Int>() {
            override fun add(element: Int): Boolean {
                return if (contains(element)) false else super.add(element)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = View.OnTouchListener { _, e ->
        if (swipedPos < 0) return@OnTouchListener false
        val point = Point(e.rawX.toInt(), e.rawY.toInt())
        val swipedViewHolder = recyclerView?.findViewHolderForAdapterPosition(swipedPos)
        val swipedItem = swipedViewHolder?.itemView
        val rect = Rect()
        swipedItem?.getGlobalVisibleRect(rect)
        if (e.action == MotionEvent.ACTION_DOWN || e.action == MotionEvent.ACTION_UP || e.action == MotionEvent.ACTION_MOVE) {
            if (rect.top < point.y && rect.bottom > point.y) {
                gestureDetector.onTouchEvent(e)
                buttons.forEach {
                    if (!it.insideRegionClick(e.x, e.y)) {
                        recoverSwipedItem(isNotifyChanged = false)
                        swipedPos = -1
                        return@forEach
                    }
                }
            } else {
                recoverQueue.add(swipedPos)
                swipedPos = -1
                recoverSwipedItem()
            }
        }
        false
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition
        if (swipedPos != pos) recoverQueue.add(swipedPos)
        swipedPos = pos
        if (buttonsBuffer.containsKey(swipedPos)) {
            buttonsBuffer[swipedPos]?.also {
                buttons.clear()
                buttons.addAll(it)
            }
        } else buttons.clear()
        buttonsBuffer.clear()
        swipeThreshold = 0.25f * buttons.size * BUTTON_WIDTH
        recoverSwipedItem()
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 0.1f * defaultValue
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 5.0f * defaultValue
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val pos = viewHolder.adapterPosition
        var translationX = dX
        val itemView = viewHolder.itemView
        if (pos < 0) {
            swipedPos = pos
            return
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val buffer: MutableList<UnderlayButton> = mutableListOf()
            if (!buttonsBuffer.containsKey(pos)) {
                instantiateUnderlayButton(viewHolder, buffer)
                buttonsBuffer[pos] = buffer
            } else {
                buttonsBuffer[pos]?.let {
                    buffer.clear()
                    buffer.addAll(it)
                }
            }
            translationX =
                dX * buffer.size * BUTTON_WIDTH / itemView.width
            drawButtons(c, itemView, buffer, pos, translationX)

        }
        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            translationX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    @Synchronized
    private fun recoverSwipedItem(isNotifyChanged: Boolean = true) {
        while (!recoverQueue.isEmpty()) {
            val pos = recoverQueue.poll()
            pos?.let {
                if (it > -1 && isNotifyChanged) {
                    recyclerView?.adapter?.notifyItemChanged(pos)
                }
            }
        }
    }

    private fun drawButtons(
        c: Canvas,
        itemView: View,
        buffer: List<UnderlayButton>,
        pos: Int,
        dX: Float
    ) {
        if (buffer.isEmpty()) return
//        if(LocaleHelper.isRTL) drawButtonsOnRight(c, itemView, buffer, pos, dX)
//        else
        drawButtonsOnLeft(c, itemView, buffer, pos, dX)
    }

    private fun drawButtonsOnLeft(
        c: Canvas,
        itemView: View,
        buffer: List<UnderlayButton>,
        pos: Int,
        dX: Float
    ) {
        if (buffer.isEmpty()) return
        var right = itemView.right.toFloat()
        val dButtonWidth = -1 * dX / buffer.size
        for (button in buffer) {
            val left = right - dButtonWidth
            button.onDraw(
                c,
                RectF(
                    left,
                    itemView.top.toFloat(),
                    right,
                    itemView.bottom.toFloat()
                ),
                pos
            )
            right = left
        }
    }

    private fun drawButtonsOnRight(
        c: Canvas,
        itemView: View,
        buffer: List<UnderlayButton>,
        pos: Int,
        dX: Float
    ) {
        if (buffer.isEmpty()) return
        var left = itemView.left.toFloat()
        val dButtonWidth = 1 * dX / buffer.size
        for (button in buffer) {
            val right = left + dButtonWidth
            button.onDraw(
                c,
                RectF(
                    left,
                    itemView.top.toFloat(),
                    right,
                    itemView.bottom.toFloat()
                ),
                pos
            )
            left = right
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    fun attachToRecyclerView(recyclerView: RecyclerView?) {
        this.recyclerView = recyclerView
        this.recyclerView?.setOnTouchListener(onTouchListener)
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(this.recyclerView)
    }

    abstract fun instantiateUnderlayButton(
        viewHolder: RecyclerView.ViewHolder?,
        underlayButtons: MutableList<UnderlayButton>
    )

    interface UnderlayButtonClickListener {
        fun onClick(pos: Int)
    }

    class UnderlayButton(
        private val context: Context,
        private val text: String,
        @DrawableRes private val imageResId: Int = 0,
        private val color: Int,
        private val clickListener: UnderlayButtonClickListener
    ) {
        private var pos = 0
        private var clickRegion: RectF? = null
        fun onClick(x: Float, y: Float): Boolean {
            if (clickRegion != null && clickRegion!!.contains(x, y)) {
                clickListener.onClick(pos)
                return true
            }
            return false
        }

        fun insideRegionClick(x: Float, y: Float): Boolean {
            clickRegion?.let {
                return it.contains(x, y)
            }
            return false
        }

        fun onDraw(c: Canvas, rect: RectF, pos: Int) {
            val p = Paint()

            p.color = ContextCompat.getColor(context, R.color.color_red)
            c.drawRect(rect, p)

            p.color = Color.WHITE
            p.textSize = Resources.getSystem().displayMetrics.density * 12

            val r = Rect()
            val cHeight = rect.height()
            val cWidth = rect.width()
            p.textAlign = Paint.Align.LEFT
            p.getTextBounds(text, 0, text.length, r)
            if (imageResId == 0) {
                val x = cWidth / 2f - r.width() / 2f - r.left
                val y = cHeight / 2f + r.height() / 2f - r.bottom

                c.drawText(text, rect.left + x, rect.top + y, p)
            } else {
                val d = ContextCompat.getDrawable(context, imageResId)
                d?.setBounds(
                    (rect.left + (rect.width() - d.intrinsicWidth) / 2).toInt(),
                    (rect.top + (rect.height() - d.intrinsicHeight) / 2).toInt(),
                    (rect.left + (rect.width() - d.intrinsicWidth) / 2).toInt() + d.intrinsicWidth,
                    (rect.top + (rect.height() - d.intrinsicHeight) / 2).toInt() + d.intrinsicHeight
                )
//
//                c.save()
//                c.scale(2f, 2f)
                d?.draw(c)
//                c.restore()

//                val bitmap = drawableToBitmap(d)
//                bitmap?.let {
////                    Log.e("TAG", "onDraw: ${rect.width()}, ${it.width}", )
//                    val withOffset = (rect.width() - it.width) / 2
//                    val heightOffset = (rect.height() - it.height) / 2
//
//                    c.drawBitmap(
//                        it,
//                        rect.left + withOffset,
//                        rect.top + heightOffset,
//                        p
//                    )
//
//                }
            }


            clickRegion = rect
            this.pos = pos
        }

        private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
            if (drawable is BitmapDrawable) return drawable.bitmap

            drawable?.let {
                val bitmap = Bitmap.createBitmap(
                    it.intrinsicWidth,
                    it.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                it.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                return bitmap
            }
            return null
        }

    }

}