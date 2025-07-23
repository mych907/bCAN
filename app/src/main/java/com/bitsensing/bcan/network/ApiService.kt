package com.bitsensing.bcan.network

import retrofit2.http.GET
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import com.bitsensing.bcan.data.CanData

//data class CanFrame(
//    val timestamp: String,
//    val frames: List<Frame>
//)

//data class Frame(
//    val id: String,
//    val name: String,
//    val data: List<Int>,
//    val dlc: Int,
//    val decoded: Map<String, String>
//)

private const val BASE_URL = "http://10.0.2.2/" // Replace with your PC's IP and port

private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface ApiService {
    @GET("/can")
    suspend fun getCanFrames(): Response<CanData>
}

object CanApi {
    val retrofitService : ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}