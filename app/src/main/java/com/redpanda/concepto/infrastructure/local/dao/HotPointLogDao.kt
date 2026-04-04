package com.redpanda.concepto.infrastructure.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.redpanda.concepto.infrastructure.local.entity.HotPointLogEntity

@Dao
interface HotPointLogDao
{
    @Insert
    suspend fun insert(point: HotPointLogEntity): Long

    @Query("SELECT * FROM hot_point_logs")
    suspend fun getAll(): List<HotPointLogEntity>
}