package com.redpanda.concepto.infrastructure.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("hot_point_logs")
data class HotPointLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pointId: Long,
    val description: String,
    val lat: Double,
    val lon: Double,
    val timestamp: Long)
