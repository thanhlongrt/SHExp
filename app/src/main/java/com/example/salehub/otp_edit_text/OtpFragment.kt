package com.example.salehub.otp_edit_text

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import com.example.salehub.base.BaseViewBindingFragment
import com.example.salehub.databinding.FragmentOtpBinding

class OtpFragment : BaseViewBindingFragment<FragmentOtpBinding>() {
    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentOtpBinding? {
        return FragmentOtpBinding.inflate(inflater, container, attachToParent)
    }

    override fun initView() {
        binding?.run {
            fillOtpView.onFullFillOtp = {
                fillOtpView.showError("Hahaha")
            }
            fillOtpView.onClickResendOtp = {
                Toast.makeText(requireContext(), "Resend OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
