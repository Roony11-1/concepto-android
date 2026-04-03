package com.redpanda.concepto.infrastructure.local.repository

import com.redpanda.concepto.domain.interfaces.repository.IHotPointLogRepository
import com.redpanda.concepto.domain.model.HotPointLog
import com.redpanda.concepto.infrastructure.local.dao.HotPointLogDao
import com.redpanda.concepto.infrastructure.local.mapper.HotPointLogMapper

class HotPointLogRepository(
    private val mapper: HotPointLogMapper = HotPointLogMapper(),
    private val dao: HotPointLogDao) : IHotPointLogRepository
{
    override suspend fun save(log: HotPointLog)
    {
        val entity = mapper.toEntity(log)

        dao.insert(entity)
    }

    override suspend fun findAll(): List<HotPointLog>
    {
        val entities = dao.getAll()

        return entities.map { entity -> mapper.toDomain(entity) }
    }
}