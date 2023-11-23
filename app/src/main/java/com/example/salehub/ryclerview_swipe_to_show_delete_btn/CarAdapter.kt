package com.example.salehub.ryclerview_swipe_to_show_delete_btn

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.salehub.databinding.ItemCarBinding

class CarAdapter(
    val onItemClick: ((Car) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mListCar = mutableListOf<Car>()

    fun setCarList(cars: List<Car>) {
        mListCar.clear()
        mListCar.addAll(cars)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyCarViewHolder(
            ItemCarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as? MyCarViewHolder)?.bind(mListCar[position])
    }

    override fun getItemCount(): Int {
        return mListCar.size
    }

    inner class MyCarViewHolder(val binding: ItemCarBinding) :
        RecyclerView.ViewHolder(binding.root) {


        fun bind(car: Car) {
            binding.tvCar.text = car.name
            binding.root.setOnClickListener {
                Log.e("TAG", "bind: $car", )
                onItemClick?.invoke(car)
            }
        }
    }
}