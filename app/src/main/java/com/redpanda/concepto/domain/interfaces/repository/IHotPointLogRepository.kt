package com.redpanda.concepto.domain.interfaces.repository

import com.redpanda.concepto.domain.model.HotPointLog

interface IHotPointLogRepository
{
    suspend fun save(log: HotPointLog): HotPointLog
    suspend fun findAll(): List<HotPointLog>
}