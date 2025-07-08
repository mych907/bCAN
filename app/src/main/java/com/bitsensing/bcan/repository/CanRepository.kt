package com.bitsensing.bcan.repository

import com.bitsensing.bcan.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class CanRepository(ipAddress: String) {
    private val retrofit = Retrofit.Builder()
        .baseUrl("http://$ipAddress:5000")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val api = retrofit.create(ApiService::class.java)

    suspend fun fetchCanFrames() = api.getCanFrames()
}
