package com.bitsensing.bcan.repository

import com.bitsensing.bcan.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class CanRepository {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://<your-pc-ip>:<port>")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val api = retrofit.create(ApiService::class.java)

    suspend fun fetchCanFrames() = api.getCanFrames()
}
