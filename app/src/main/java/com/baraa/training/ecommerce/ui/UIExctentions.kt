package com.baraa.training.ecommerce.ui

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.baraa.training.ecommerce.R


fun View.showSnakeBarError(message: String) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(this.context.resources.getString(R.string.ok)) {}
        .setActionTextColor(ContextCompat.getColor(this.context, R.color.white))
        .show()
}

fun View.showSnakeBarLoggedIn(message: String = "Logged in successfully!") {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(this.context.resources.getString(R.string.ok)) {}
        .setActionTextColor(ContextCompat.getColor(this.context, R.color.white))
        .show()
}

fun View.showSnakeBarRegistered(message: String = "Registered successfully!") {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(this.context.resources.getString(R.string.ok)) {}
        .setActionTextColor(ContextCompat.getColor(this.context, R.color.white))
        .show()
}

fun View.showRetrySnakeBarError(message: String, retry: () -> Unit) {
    Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
        .setAction(this.context.resources.getString(R.string.retry)) { retry.invoke() }
        .setActionTextColor(ContextCompat.getColor(this.context, R.color.white))
        .show()
}