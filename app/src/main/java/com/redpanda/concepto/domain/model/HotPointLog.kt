package com.redpanda.concepto.domain.model

data class HotPointLog(
    val id: Long,
    val pointId: Long,
    val description: String,
    val lat: Double,
    val lon: Double,
    val timestamp: Long)
