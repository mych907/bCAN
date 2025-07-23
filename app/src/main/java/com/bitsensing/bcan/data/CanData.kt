package com.bitsensing.bcan.data

import kotlinx.serialization.Serializable

@Serializable
data class CanData(
    val id: String,
    val speed: Int,
    val rpm: Int
)