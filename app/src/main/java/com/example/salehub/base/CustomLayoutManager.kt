package com.example.salehub.base

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 * Created by Thanh Long Nguyen on 6/16/2021
 */
@SuppressLint("WrongConstant")
class CustomLayoutManager
    (
    context: Context?,
    orientation: Int = VERTICAL,
    reverseLayout: Boolean = false
) : LinearLayoutManager(context, orientation, reverseLayout) {

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
        updateChildrenAlpha()
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        updateChildrenAlpha()
        return super.scrollVerticallyBy(dy, recycler, state)
    }

    private fun updateChildrenAlpha() {
        for(i in 0..childCount){
            if (i==childCount/2){
                getChildAt(i)?.alpha = 1f
            } else {
                getChildAt(i)?.alpha = 0.3f
            }
        }
    }
}