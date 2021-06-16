package com.example.salehub

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Thanh Long Nguyen on 6/16/2021
 */
class PickerAdapter(
    val data: List<Month> = listOf()
) : RecyclerView.Adapter<PickerAdapter.ViewHolder>() {
    inner class ViewHolder(item: View) : RecyclerView.ViewHolder(item)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_picker, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.tv_picker).text = data[position].textName
        if (data[position].isSelected) {
            holder.itemView.findViewById<TextView>(R.id.tv_picker).setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.design_default_color_primary
                )
            )
        } else {
            holder.itemView.findViewById<TextView>(R.id.tv_picker).setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.black
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    fun markAsSelected(position: Int) {
        data.forEach { it.isSelected = false }
        data[position].isSelected = true
        notifyDataSetChanged()
    }

    enum class Month(
        var isSelected: Boolean = false,
         val textName: String = ""
    ) {
        None, Jan(textName = "Jan"),
        Feb(textName = "Feb"),
        Mar(textName = "Mar"),
        Apr(textName = "Apr"),
        May(textName = "May"),
        Jun(textName = "Jun"),
        Jul(textName = "Jul"),
        Aug(textName = "Aug"),
        Sep(textName = "Sep"),
        Oct(textName = "Oct"),
        Nov(textName = "Nov"),
        Dec(textName = "Dec")
    }
}