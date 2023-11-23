/*
 * Copyright 2018 Keval Patel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance wit
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for
 *  the specific language governing permissions and limitations under the License.
 */
package com.example.salehub.ruler_picker

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.widget.HorizontalScrollView
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.example.salehub.ruler_picker.ObservableHorizontalScrollView.ScrollChangedListener

/**
 * Created by Kevalpatel2106 on 29-Mar-2018.
 * A [HorizontalScrollView] which has ability to detect start/stop scrolling.
 *
 * @see [](https://github.com/dwfox/DWRulerView>Original Repo</a>
) */
@SuppressLint("ViewConstructor")
internal class ObservableHorizontalScrollView
/**
 * Constructor.
 *
 * @param context  [Context] of caller.
 * @param listener [ScrollChangedListener] to get callbacks when scroll starts or stops.
 * @see ScrollChangedListener
 */(
    @NonNull context: Context?,
    @field:Nullable @param:NonNull private val mScrollChangedListener: ScrollChangedListener?
) :
    HorizontalScrollView(context) {
    private var mLastScrollUpdateMills: Long = -1
    private var initialX = -1
    private var isUserTouching = false
    private val mScrollerTask: Runnable = object : Runnable {
        override fun run() {
            if (System.currentTimeMillis() - mLastScrollUpdateMills > NEW_CHECK_DURATION && !isUserTouching) {
                mLastScrollUpdateMills = -1
                mScrollChangedListener!!.onScrollStopped()
            } else {
                //Post next delay
                postDelayed(this, NEW_CHECK_DURATION)
            }
//            val updatedY = scrollX
//            if (updatedY == initialX) {
//                //we've stopped
//                mScrollChangedListener?.onScrollStopped()
//            } else {
//                initialX = updatedY
////                postDelayed(this, NEW_CHECK_DURATION)
//            }
        }
    }

    override fun onScrollChanged(
        horizontalOrigin: Int,
        verticalOrigin: Int,
        oldHorizontalOrigin: Int,
        oldVerticalOrigin: Int
    ) {
        super.onScrollChanged(
            horizontalOrigin,
            verticalOrigin,
            oldHorizontalOrigin,
            oldVerticalOrigin
        )


//        if (scrollX == 0) {
////            scrollX = getChildAt(0).right - width
//            Log.e("LongNT52", "onScrollChanged = 0: ${getChildAt(0).left}", )
//
//        }
//        Log.e("LongNT52", "onScrollChanged left: ${getChildAt(0).left}", )
//        Log.e("LongNT52", "onScrollChanged right: ${getChildAt(0).right}", )
//        Log.e("LongNT52", "onScrollChanged width: ${width}", )
        if (mScrollChangedListener == null) return
        mScrollChangedListener.onScrollChanged()
        if (mLastScrollUpdateMills == -1L) postDelayed(mScrollerTask, NEW_CHECK_DURATION)
        mLastScrollUpdateMills = System.currentTimeMillis()
    }

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
//        if (ev?.action == MotionEvent.ACTION_UP) {
////            if (mLastScrollUpdateMills == -1L) postDelayed(mScrollerTask, NEW_CHECK_DURATION)
////            mLastScrollUpdateMills = System.currentTimeMillis()
//        }
        when (ev?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                isUserTouching = true
            }
            else -> {
                isUserTouching = false
            }
        }
        return super.onTouchEvent(ev)
    }

    private fun checkIfScrollStopped() {
        initialX = scrollY
        postDelayed({
            val updatedY = scrollY
            if (updatedY == initialX) {
                //we've stopped
                mScrollChangedListener?.onScrollStopped()
            } else {
                initialX = updatedY
                checkIfScrollStopped()
            }
        }, 5000)
    }

    /**
     * Listener to get callbacks on scrollview scroll events.
     */
    internal interface ScrollChangedListener {
        /**
         * Called upon change in scroll position.
         */
        fun onScrollChanged()

        /**
         * Called when the scrollview stops scrolling.
         */
        fun onScrollStopped()
    }

    companion object {
        private const val NEW_CHECK_DURATION = 50L
    }
}