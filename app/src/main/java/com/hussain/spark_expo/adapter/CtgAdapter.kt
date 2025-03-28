package com.hussain.spark_expo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hussain.spark_expo.R
import com.hussain.spark_expo.model.CategoryModel

class CtgAdapter(
    private var categories: List<CategoryModel>,
    private val onEdit: (CategoryModel) -> Unit,
    private val onDelete: (CategoryModel) -> Unit
) : RecyclerView.Adapter<CtgAdapter.CtgViewHolder>() {

    class CtgViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serialNumber: TextView = view.findViewById(R.id.serialNumber)
        val name: TextView = view.findViewById(R.id.name)
        val edit: ImageView = view.findViewById(R.id.edit)
        val delete: ImageView = view.findViewById(R.id.delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CtgViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category, parent, false)
        return CtgViewHolder(view)
    }

    override fun onBindViewHolder(holder: CtgViewHolder, position: Int) {
        val category = categories[position]
        holder.serialNumber.text = (position + 1).toString()
        holder.name.text = category.name

        holder.edit.setOnClickListener { onEdit(category) }
        holder.delete.setOnClickListener { onDelete(category) }
    }

    override fun getItemCount() = categories.size

    fun updateList(newList: List<CategoryModel>) {
        categories = newList
        notifyDataSetChanged()
    }
}
