package com.davidnardya.shifts.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "guards_table")
data class Guard(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    var name: String? = "",
    var offTime: List<OffTime>? = null
)
