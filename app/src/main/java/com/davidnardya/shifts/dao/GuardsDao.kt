package com.davidnardya.shifts.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.davidnardya.shifts.models.Guard

@Dao
interface GuardsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGuard(guard: Guard)

    @Delete
    suspend fun deleteGuard(guard: Guard)

    @Query("SELECT * FROM guards_table")
    suspend fun fetchGuards(): List<Guard>?

    @Query("DELETE FROM guards_table")
    suspend fun deleteAllGuards()
}