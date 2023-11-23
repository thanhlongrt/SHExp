package com.example.salehub.step_reycler_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.salehub.databinding.ItemStepBinding

class StepAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mListData = mutableListOf<String>()

    fun setDataList(data: List<String>) {
        mListData.clear()
        mListData.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return StepViewHolder(
            ItemStepBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? StepViewHolder)?.bind(mListData[position], position)
    }

    override fun getItemCount(): Int {
        return mListData.size
    }

    inner class StepViewHolder(val binding: ItemStepBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(text: String, position: Int) {
            binding.run {
                tvTitle.text = text
                tvStep.text = (position + 1).toString()
                dashViewBottom.isVisible = position != mListData.lastIndex
            }
        }
    }
}