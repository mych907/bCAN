package com.bitsensing.bcan.network

import retrofit2.http.GET
import retrofit2.Response

data class CanFrame(
    val timestamp: String,
    val frames: List<Frame>
)

data class Frame(
    val id: String,
    val name: String,
    val data: List<Int>,
    val dlc: Int,
    val decoded: Map<String, String>
)

interface ApiService {
    @GET("/can")
    suspend fun getCanFrames(): Response<CanFrame>
}
