package com.davidnardya.shifts.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.davidnardya.shifts.dao.GuardsDao
import com.davidnardya.shifts.models.Guard


@Database(entities = [Guard::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class GuardsDataBase : RoomDatabase() {

    abstract fun guardsDao(): GuardsDao

    companion object {
        @Volatile
        private var instance: GuardsDataBase? = null

        fun getDatabase(context: Context): GuardsDataBase =
            instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it
                }
            }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, GuardsDataBase::class.java, "guards_database")
                .fallbackToDestructiveMigration()
                .build()
    }
}