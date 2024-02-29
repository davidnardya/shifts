package com.davidnardya.shifts.db

import androidx.room.TypeConverter
import com.davidnardya.shifts.models.ShiftDay
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    @TypeConverter
    fun fromString(value: String): List<ShiftDay> {
        val listType = object : TypeToken<List<ShiftDay>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toString(list: List<ShiftDay>): String {
        return Gson().toJson(list)
    }
}