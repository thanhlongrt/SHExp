package com.example.salehub.ratingbar


import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import com.example.salehub.R


class MyRatingBar(context: Context, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatRatingBar(context, attrs) {
    private val starDrawable: Drawable = resources.getDrawable(
        R.drawable.ratingbar, context.theme
    )

    constructor(context: Context) : this(context, null) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // Make sure to account for padding and margin, if you care.
        // I only cared about left padding.
        setMeasuredDimension(
            starDrawable.intrinsicWidth * 5
                    + paddingLeft, starDrawable.intrinsicHeight
        )
    }

}