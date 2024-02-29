package com.davidnardya.shifts.utils

import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter


class ListToFileHelper {
    val TAG = this@ListToFileHelper.javaClass.simpleName

    // Function to save CSV data to a file
    fun saveToFile(data: String, fileName: String): Boolean {
        return try {
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, "$fileName.txt")
            FileOutputStream(file).use { fos ->
                OutputStreamWriter(fos, Charsets.UTF_8).use { osw ->
                    osw.write(data)
                }
            }
            true // File saved successfully
        } catch (e: IOException) {
            Log.d(TAG, e.message.toString())
            false // Error occurred while saving file
        }
    }
}