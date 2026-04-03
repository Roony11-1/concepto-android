package com.redpanda.concepto.infrastructure.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.redpanda.concepto.infrastructure.local.dao.HotPointDao
import com.redpanda.concepto.infrastructure.local.dao.HotPointLogDao
import com.redpanda.concepto.infrastructure.local.entity.HotPointEntity
import com.redpanda.concepto.infrastructure.local.entity.HotPointLogEntity

@Database(
    entities = [HotPointEntity::class, HotPointLogEntity::class],
    version = 2)
abstract class AppDbContext : RoomDatabase()
{
    abstract fun hotPointDao(): HotPointDao
    abstract fun  HotPointLogDao(): HotPointLogDao
}