package com.redpanda.concepto.infrastructure.local.repository

import com.redpanda.concepto.domain.interfaces.repository.IHotPointRepository
import com.redpanda.concepto.domain.model.HotPoint
import com.redpanda.concepto.infrastructure.local.dao.HotPointDao
import com.redpanda.concepto.infrastructure.local.mapper.HotPointMapper

class HotPointRepository(
    private val mapper: HotPointMapper = HotPointMapper(),
    private val dao: HotPointDao) : IHotPointRepository
{
    override suspend fun save(point: HotPoint)
    {
        val entity = mapper.toEntity(point)

        dao.insert(entity)
    }

    override suspend fun getAll(): List<HotPoint>
    {
        val entities = dao.getAll()

        return entities.map { entity ->  mapper.toDomain(entity) }
    }

    override suspend fun getById(id: Int): HotPoint?
    {
        val entity = dao.getById(id)

        return entity?.let { mapper.toDomain(it) }
    }

    override suspend fun getByLocation(lat: Double, lon: Double): HotPoint?
    {
        val entity = dao.getByLocation(lat, lon)

        return entity?.let { mapper.toDomain(it) }
    }

    override suspend fun deleteById(id: Int)
    {
        dao.deleteById(id)
    }
}