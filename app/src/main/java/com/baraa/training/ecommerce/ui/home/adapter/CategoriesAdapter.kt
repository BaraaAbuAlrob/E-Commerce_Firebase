package com.baraa.training.ecommerce.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baraa.training.ecommerce.databinding.CategoryItemBinding
import com.baraa.training.ecommerce.ui.common.views.getGlideCircleLoading
import com.baraa.training.ecommerce.ui.home.model.CategoryUIModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterInside

class CategoriesAdapter(
    private val categories: List<CategoryUIModel>
) : RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val binding: CategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: CategoryUIModel) {
            binding.category = category
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