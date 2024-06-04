package com.baraa.training.ecommerce.utils

import android.util.Patterns


// create extension function for email validation
fun String.isValidEmail(): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(this).matches()
}