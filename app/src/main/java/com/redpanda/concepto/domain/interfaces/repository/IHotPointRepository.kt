package com.redpanda.concepto.domain.interfaces.repository

import com.redpanda.concepto.domain.model.HotPoint

interface IHotPointRepository
{
    suspend fun save(point: HotPoint)
    suspend fun getAll(): List<HotPoint>
    suspend fun getById(id: Int): HotPoint?
    suspend fun getByLocation(lat: Double, lon: Double): HotPoint?
    suspend fun deleteById(id: Int)
}