package com.example.salehub.otp_edit_text

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.example.salehub.databinding.LayoutFillOtpBinding

class FillOtpView(context: Context, attributeSet: AttributeSet) :
    FrameLayout(context, attributeSet) {

    private val binding = LayoutFillOtpBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    var onFullFillOtp: ((String) -> Unit)? = null
        set(value) {
            binding.edtOtp.onFullFillOtp = value
            field = value
        }

    var onClickResendOtp: (() -> Unit)? = null

    init {
        binding.tvResendOtp.setOnClickListener {
            onClickResendOtp?.invoke()
        }
    }

    fun showError(error: String) {
        binding.run {
            edtOtp.state = OtpEditText.State.Error
            tvError.text = error
            tvError.isVisible = true
        }
    }

    fun hideError() {
        binding.run {
            edtOtp.state = OtpEditText.State.Normal
            tvError.isVisible = false
        }
    }
}
