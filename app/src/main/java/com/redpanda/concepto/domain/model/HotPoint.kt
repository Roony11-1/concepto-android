package com.redpanda.concepto.domain.model

data class HotPoint(
    val id: Int,
    val lat: Double,
    val lon: Double,
    val description: String)