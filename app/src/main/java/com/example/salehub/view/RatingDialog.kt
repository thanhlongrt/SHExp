package com.example.salehub.view

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.salehub.R
import com.example.salehub.databinding.DialogRatingSalehubBinding
import kotlin.math.roundToInt

class RatingDialog(
    val ctx: Context,
    val onConfirm: (Int) -> Unit
) : Dialog(ctx, R.style.AppDialog) {

    private var binding: DialogRatingSalehubBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        binding = DialogRatingSalehubBinding.inflate(LayoutInflater.from(ctx))
        binding?.root?.let { setContentView(it) }
        initView()
    }

    private fun initView() {
        binding?.btnConfirm?.setOnClickListener {
            onConfirm.invoke(binding?.ratingBar?.rating?.roundToInt() ?: 0)
        }
    }
}