package com.redpanda.concepto.domain.model

data class HotPoint(
    val id: Long,
    val lat: Double,
    val lon: Double,
    val description: String)