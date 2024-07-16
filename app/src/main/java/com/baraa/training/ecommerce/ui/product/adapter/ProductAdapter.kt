package com.baraa.training.ecommerce.ui.product.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.baraa.training.ecommerce.databinding.ProductItemLayoutBinding
import com.baraa.training.ecommerce.ui.products.model.ProductUIModel

class ProductAdapter : ListAdapter<ProductUIModel, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ProductItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        Log.d("ProductAdapter", "onBindViewHolder: ${product.id}")
        holder.bind(product)
    }

    class ProductViewHolder(private val binding: ProductItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductUIModel) {
            binding.product = product
            binding.executePendingBindings() // This ensures that the binding has been executed immediately.
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<ProductUIModel>() {
        override fun areItemsTheSame(oldItem: ProductUIModel, newItem: ProductUIModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductUIModel, newItem: ProductUIModel): Boolean {
            return oldItem == newItem
        }
    }
}