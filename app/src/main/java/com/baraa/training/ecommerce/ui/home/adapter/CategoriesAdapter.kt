package com.baraa.training.ecommerce.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.baraa.training.ecommerce.databinding.CategoryItemBinding
import com.baraa.training.ecommerce.ui.home.model.CategoryUIModel

class CategoriesAdapter(
    private var categories: List<CategoryUIModel> = emptyList(),
    private val onCategoryClick: (CategoryUIModel) -> Unit = {}
) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    fun submitList(newList: List<CategoryUIModel>) {
        categories = newList
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(private val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryUIModel) {
            binding.category = category
            binding.root.setOnClickListener { onCategoryClick(category) }
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount(): Int = categories.size
}