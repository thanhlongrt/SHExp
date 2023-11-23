package com.example.salehub.step_reycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.salehub.base.BaseViewBindingFragment
import com.example.salehub.databinding.FragmentStepRvBinding
import com.example.salehub.utils.Utils

class StepRVFragment : BaseViewBindingFragment<FragmentStepRvBinding>() {
    override fun initView() {
        setMarginTop()
        setupRecyclerView()

    }

    private fun setupRecyclerView() {
        binding?.stepRecyclerView?.run {
            layoutManager = LinearLayoutManager(context)
            adapter = StepAdapter().apply {
                setDataList(
                    listOf(
                        "Stop in a safe place where",
                        "Stop in a safe place where there are no flammable objects",
                        "Stop in a safe place where there are no flammable objects there are no flammable objects",
                        "Stop in a safe place where there are no flammable objects there are no flammable objects there are no flammable objects there are no flammable objects there",
                        "Stop in a safe place where there are no flammable objects there are no flammable objects there are no flammable objects there are no flammable objects there are no flammable objects",
                        "Stop in a safe place where there are no flammable objects there are no flammable objects there are no flammable objects there are no flammable objects there are no flammable objects there are no flammable objects there are no flammable objects there are no flammable objects there are no flammable objects there are no flammable objects there are no flammable objects there are no flammable objects there are no flammable objects there are no flammable objects there are no flammable objects",
                        "Stop in a safe place where",
                        )
                )
            }
        }
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentStepRvBinding {
        return FragmentStepRvBinding.inflate(inflater, container, attachToParent)
    }

    private fun setMarginTop() {
        (binding?.stepRecyclerView?.layoutParams as? ConstraintLayout.LayoutParams)?.run {
            topMargin = Utils.getStatusBarHeight()
            binding?.stepRecyclerView?.layoutParams = this
        }
    }
}