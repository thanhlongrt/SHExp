package com.example.salehub.ratingbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.salehub.databinding.FragmentRatingBarBinding
import kotlin.math.ceil
import kotlin.math.roundToInt

class RatingBarFragment : Fragment() {
    var binding: FragmentRatingBarBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRatingBarBinding.inflate(inflater, container, false)
        return binding?.root!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.btnShowRatingDialog?.setOnClickListener {
//            mRatingDialog = context?.let { it1 ->
//                RatingDialog(it1) { rating ->
//                    Toast.makeText(context, "$rating", Toast.LENGTH_LONG).show()
//                }.apply {
//                    show()
//                }
//            }
        }

        binding?.ratingBar?.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
            Toast.makeText(context, "$rating", Toast.LENGTH_LONG).show()
            if (fromUser){
                ratingBar.rating = ceil(rating)
            }

        }
    }
}