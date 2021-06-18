package com.example.salehub

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Thanh Long Nguyen on 6/13/2021
 */
class SalehubHomeFragment : Fragment() {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: PickerAdapter
    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mNumberPicker: CustomNumberPicker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_salehub_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecyclerView = view.findViewById(R.id.rv_picker)
        val data = listOf(
            PickerAdapter.Month.None,
            PickerAdapter.Month.Jan,
            PickerAdapter.Month.Feb,
            PickerAdapter.Month.Mar,
            PickerAdapter.Month.Apr,
            PickerAdapter.Month.May,
            PickerAdapter.Month.Jun,
            PickerAdapter.Month.Jul,
            PickerAdapter.Month.Aug,
            PickerAdapter.Month.Sep,
            PickerAdapter.Month.Oct,
            PickerAdapter.Month.Nov,
            PickerAdapter.Month.Dec,
            PickerAdapter.Month.None,
        )
        mAdapter = PickerAdapter(
            data
        )
        mLayoutManager = LinearLayoutManager(context)
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(mRecyclerView)
        mRecyclerView.apply {
            layoutManager = mLayoutManager
            adapter = mAdapter
            mAdapter.markAsSelected(1)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                @RequiresApi(Build.VERSION_CODES.M)
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        snapHelper.findSnapView(mLayoutManager)?.let {
                            val pos = getChildAdapterPosition(it)
                            mAdapter.markAsSelected(pos)
//                            Log.e("SalehubHomeFragment", "onScrollStateChanged: ${data[pos]}")
//                            Toast.makeText(context, data[pos].textName, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            })
        }

        mNumberPicker = view.findViewById(R.id.np_picker)
        mNumberPicker.maxValue = data.size - 1
        mNumberPicker.displayedValues = data.map { it.textName }.toTypedArray()
        mNumberPicker.wrapSelectorWheel = false
    }

}