package com.example.planix.Data

import android.content.Context
import androidx.room.*
import com.example.planix.Data.Converters

@Database(entities = [TaskEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tasks_db"
                )
                    .fallbackToDestructiveMigration() // удаляет старую БД при несовпадении схем
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
