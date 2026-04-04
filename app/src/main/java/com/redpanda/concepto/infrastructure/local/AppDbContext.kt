package com.redpanda.concepto.infrastructure.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.redpanda.concepto.infrastructure.local.dao.HotPointDao
import com.redpanda.concepto.infrastructure.local.dao.HotPointLogDao
import com.redpanda.concepto.infrastructure.local.entity.HotPointEntity
import com.redpanda.concepto.infrastructure.local.entity.HotPointLogEntity

@Database(
    entities = [HotPointEntity::class, HotPointLogEntity::class],
    version = 3)
abstract class AppDbContext : RoomDatabase()
{
    abstract fun hotPointDao(): HotPointDao
    abstract fun  HotPointLogDao(): HotPointLogDao

    companion object
    {
        @Volatile
        private var INSTANCE: AppDbContext? = null

        fun getDatabase(context: Context): AppDbContext
        {
            return INSTANCE ?: synchronized(this)
            {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDbContext::class.java,
                    "app_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}