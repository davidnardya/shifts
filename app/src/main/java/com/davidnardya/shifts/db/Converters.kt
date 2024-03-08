package com.davidnardya.shifts.db

import androidx.room.TypeConverter
import com.davidnardya.shifts.models.OffTime
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromString(value: String): List<OffTime> {
        val listType = object : TypeToken<List<OffTime>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toString(list: List<OffTime>): String {
        return Gson().toJson(list)
    }
}