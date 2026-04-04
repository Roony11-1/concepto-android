package com.redpanda.concepto.infrastructure.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.redpanda.concepto.infrastructure.local.entity.HotPointEntity

@Dao
interface HotPointDao
{
    @Insert
    suspend fun insert(entity: HotPointEntity): Long

    @Query("SELECT * FROM hot_points")
    suspend fun getAll(): List<HotPointEntity>

    @Query("SELECT * FROM hot_points WHERE id = :id")
    suspend fun getById(id: Long): HotPointEntity?

    @Query("SELECT * FROM hot_points WHERE lat = :lat AND lon = :lon LIMIT 1")
    suspend fun getByLocation(lat: Double, lon: Double): HotPointEntity?

    @Query("DELETE FROM hot_points WHERE id = :id")
    suspend fun deleteById(id: Long)
}