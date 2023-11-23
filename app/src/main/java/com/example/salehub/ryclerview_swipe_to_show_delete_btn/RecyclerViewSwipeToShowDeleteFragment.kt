package com.example.salehub.ryclerview_swipe_to_show_delete_btn

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.canon.cebm.miniprint.android.us.scenes.menu.adapter.SwipeToDeleteHelper
import com.example.salehub.R
import com.example.salehub.base.BaseViewBindingFragment
import com.example.salehub.databinding.FragmentRecyclerviewSwipeToShowDeleteBinding

class RecyclerViewSwipeToShowDeleteFragment :
    BaseViewBindingFragment<FragmentRecyclerviewSwipeToShowDeleteBinding>() {
    override fun initView() {


        setupListCar()

    }

    private fun setupListCar() {
        binding?.recyclerView?.run {
            layoutManager = LinearLayoutManager(context)
            adapter = CarAdapter().apply {
                setCarList(
                    (1..50).map {
                        Car("Car #$it")
                    }
                )
            }
        }
        val swiperHelper = object : SwipeToDeleteHelper(context,) {
            override fun instantiateUnderlayButton(
                viewHolder: RecyclerView.ViewHolder?,
                underlayButtons: MutableList<UnderlayButton>
            ) {
                underlayButtons.add(UnderlayButton(
                    requireContext(),
                    "Delete",
                    R.drawable.ic_notification,
                    Color.parseColor("#FF3C30"),
                    object : UnderlayButtonClickListener {
                        override fun onClick(pos: Int) {
                            Toast.makeText(
                                requireContext(),
                                "Position: $pos",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                ))
            }
        }
        swiperHelper.attachToRecyclerView(binding?.recyclerView)
    }

    override fun inflateViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentRecyclerviewSwipeToShowDeleteBinding? {
        return FragmentRecyclerviewSwipeToShowDeleteBinding.inflate(
            inflater,
            container,
            attachToParent
        )
    }


}