package com.baraa.training.ecommerce.data.repository.cart

import android.util.Log
import com.baraa.training.ecommerce.data.models.cart.CartItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : CartRepository {

    private val localCartList = mutableListOf<CartItemModel>()

    init {
        // Initial sample items for demonstration if cart is empty
        localCartList.add(
            CartItemModel(
                id = "c1",
                productId = "p1",
                productName = "Nike Air Max 270 React",
                productImage = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=800",
                price = 299,
                quantity = 1,
                selectedColor = "#40BFFF",
                selectedSize = "M"
            )
        )
        localCartList.add(
            CartItemModel(
                id = "c2",
                productId = "p2",
                productName = "QUAPRI Leather Backpack",
                productImage = "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=800",
                price = 199,
                quantity = 1,
                selectedColor = "#223263",
                selectedSize = "One Size"
            )
        )
    }

    override fun getCartItems(): Flow<List<CartItemModel>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            trySend(localCartList.toList())
            awaitClose {}
            return@callbackFlow
        }

        val listener = firestore.collection("users")
            .document(userId)
            .collection("cart")
            .addSnapshotListener { value, error ->
                if (error != null || value == null || value.isEmpty) {
                    Log.d("CartRepositoryImpl", "Using local cart list: ${localCartList.size}")
                    trySend(localCartList.toList())
                    return@addSnapshotListener
                }

                val items = value.toObjects(CartItemModel::class.java)
                trySend(items)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun addToCart(item: CartItemModel) {
        val existingIndex = localCartList.indexOfFirst { it.productId == item.productId && it.selectedSize == item.selectedSize }
        if (existingIndex >= 0) {
            localCartList[existingIndex].quantity += item.quantity
        } else {
            localCartList.add(item)
        }

        val userId = auth.currentUser?.uid ?: return
        try {
            val docRef = if (item.id.isEmpty()) {
                firestore.collection("users").document(userId).collection("cart").document()
            } else {
                firestore.collection("users").document(userId).collection("cart").document(item.id)
            }
            item.id = docRef.id
            docRef.set(item).await()
        } catch (e: Exception) {
            Log.e("CartRepositoryImpl", "Error saving cart item to Firestore", e)
        }
    }

    override suspend fun updateQuantity(cartItemId: String, newQuantity: Int) {
        val item = localCartList.find { it.id == cartItemId }
        if (item != null) {
            if (newQuantity <= 0) {
                localCartList.remove(item)
            } else {
                item.quantity = newQuantity
            }
        }

        val userId = auth.currentUser?.uid ?: return
        try {
            if (newQuantity <= 0) {
                firestore.collection("users").document(userId).collection("cart").document(cartItemId).delete().await()
            } else {
                firestore.collection("users").document(userId).collection("cart").document(cartItemId).update("quantity", newQuantity).await()
            }
        } catch (e: Exception) {
            Log.e("CartRepositoryImpl", "Error updating cart quantity", e)
        }
    }

    override suspend fun removeFromCart(cartItemId: String) {
        localCartList.removeAll { it.id == cartItemId }
        val userId = auth.currentUser?.uid ?: return
        try {
            firestore.collection("users").document(userId).collection("cart").document(cartItemId).delete().await()
        } catch (e: Exception) {
            Log.e("CartRepositoryImpl", "Error removing cart item", e)
        }
    }

    override suspend fun clearCart() {
        localCartList.clear()
        val userId = auth.currentUser?.uid ?: return
        try {
            val snapshot = firestore.collection("users").document(userId).collection("cart").get().await()
            for (doc in snapshot.documents) {
                doc.reference.delete().await()
            }
        } catch (e: Exception) {
            Log.e("CartRepositoryImpl", "Error clearing cart", e)
        }
    }
}
