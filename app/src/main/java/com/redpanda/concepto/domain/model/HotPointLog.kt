package com.redpanda.concepto.domain.model

data class HotPointLog(
    val id: Int,
    val pointId: Int,
    val description: String,
    val lat: Double,
    val lon: Double,
    val timestamp: Long = System.currentTimeMillis())
