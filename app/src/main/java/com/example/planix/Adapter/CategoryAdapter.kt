package com.example.planix.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.planix.Domain.CategoryModel
import com.example.planix.databinding.ViewholderCategoryBinding

class CategoryAdapter(
    private val categoryList: List<CategoryModel>,
    private val onClick: (CategoryModel) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(val binding: ViewholderCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ViewholderCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.binding.titleTxt.text = category.name

        Glide.with(holder.itemView.context)
            .load(category.imageResId)
            .into(holder.binding.img)

        holder.itemView.setOnClickListener {
            onClick(category)
        }
    }
}
