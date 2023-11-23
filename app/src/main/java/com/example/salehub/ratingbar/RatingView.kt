package com.example.salehub.ratingbar

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.salehub.R

class RatingView : LinearLayout {

    companion object {
        const val DEFAULT_NUMBER_OF_STARS = 5
        const val DEFAULT_STEP_SIZE = 1
    }

    private var mNumStar = DEFAULT_NUMBER_OF_STARS
    private var mStepSize = DEFAULT_STEP_SIZE
    private var mRating: Int = 0
    var onItemClick: ((Int) -> Unit)? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        repeat(mNumStar) { position ->
            val starView = ImageView(context)
            starView.setImageResource(R.drawable.ic_star_black)
            starView.tag = position
            starView.setOnClickListener {
                onItemClick?.invoke(position)
                (0..position).forEach {
                    (getChildAt(it) as? ImageView)?.setImageResource(R.drawable.ic_star_yellow)
                    (getChildAt(it) as? ImageView)?.setImageLevel(500)
                }
                ((position+1) until mNumStar).forEach {
                    (getChildAt(it) as? ImageView)?.setImageResource(R.drawable.ic_star_black)
                }
            }
            addView(starView)
        }
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


}