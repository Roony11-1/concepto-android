package com.redpanda.concepto.infrastructure.local.mapper

import com.redpanda.concepto.domain.model.HotPoint
import com.redpanda.concepto.infrastructure.local.entity.HotPointEntity

class HotPointMapper
{
    fun toDomain(entity: HotPointEntity) =
        HotPoint(entity.id, entity.lat, entity.lon, entity.description)

    fun toEntity(domain: HotPoint) =
        HotPointEntity(domain.id, domain.lat, domain.lon, domain.description)
}