package com.davidnardya.shifts.utils

import android.widget.Toast
import com.davidnardya.shifts.App

fun showToast(message: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(App.instance, message, length).show()
}