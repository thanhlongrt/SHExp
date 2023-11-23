package com.example.salehub.account

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.salehub.databinding.ItemMyAccountBinding

class MyAccountAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val mDataList = mutableListOf<String>()
    fun setDataList(data: List<String>) {
        mDataList.clear()
        mDataList.addAll(data)
        notifyDataSetChanged()
    }

    inner class AccountVH(val binding: ItemMyAccountBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(name: String) {
            binding.tvAccount.text = name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return AccountVH(
            ItemMyAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? AccountVH)?.bind(mDataList[position])
    }

    override fun getItemCount(): Int = mDataList.size
}