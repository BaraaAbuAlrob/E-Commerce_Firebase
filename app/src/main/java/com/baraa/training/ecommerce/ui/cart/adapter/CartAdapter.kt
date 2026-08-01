package com.baraa.training.ecommerce.ui.cart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.baraa.training.ecommerce.data.models.cart.CartItemModel
import com.baraa.training.ecommerce.databinding.ItemCartProductBinding

class CartAdapter(
    private val onQuantityChanged: (CartItemModel, Int) -> Unit,
    private val onItemDeleted: (CartItemModel) -> Unit
) : ListAdapter<CartItemModel, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartProductBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: ItemCartProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: CartItemModel) {
            binding.cartProductTitleTv.text = item.productName
            binding.cartProductPriceTv.text = "$${item.price}.00"
            binding.cartQuantityTv.text = item.quantity.toString()

            Glide.with(binding.root.context)
                .load(item.productImage)
                .into(binding.cartProductImg)

            binding.btnPlus.setOnClickListener {
                onQuantityChanged(item, item.quantity + 1)
            }

            binding.btnMinus.setOnClickListener {
                onQuantityChanged(item, item.quantity - 1)
            }

            binding.btnDeleteCartItem.setOnClickListener {
                onItemDeleted(item)
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItemModel>() {
        override fun areItemsTheSame(oldItem: CartItemModel, newItem: CartItemModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartItemModel, newItem: CartItemModel): Boolean {
            return oldItem == newItem
        }
    }
}
