package com.redpanda.concepto.infrastructure.local.mapper

import com.redpanda.concepto.domain.model.HotPointLog
import com.redpanda.concepto.infrastructure.local.entity.HotPointLogEntity

class HotPointLogMapper
{
    fun toDomain(entity: HotPointLogEntity): HotPointLog = HotPointLog(
            id = entity.id,
            pointId = entity.pointId,
            description = entity.description,
            lat = entity.lat,
            lon = entity.lon,
            timestamp = entity.timestamp)

    fun toEntity(domain: HotPointLog): HotPointLogEntity = HotPointLogEntity(
            id = domain.id,
            pointId = domain.pointId,
            description = domain.description,
            lat = domain.lat,
            lon = domain.lon,
            timestamp = domain.timestamp)
}