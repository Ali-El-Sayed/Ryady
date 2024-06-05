package com.example.ryady.view.extensions

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentActivity

fun <T> FragmentActivity.move(context: Context, destination: Class<T>) {
    val intent = Intent(context, destination)
    startActivity(intent)
}