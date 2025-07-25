package com.bitsensing.bcan.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CanData(
    val id: Int,

    @SerialName("raw_data")
    val rawData: String,

    @SerialName("decoded_data")
    val decodedData: DecodedData
)

@Serializable
data class DecodedData(
    @SerialName("FL_WheelSpeed")
    val flWheelSpeed: Double,

    @SerialName("FR_WheelSpeed")
    val frWheelSpeed: Double,

    @SerialName("RL_WheelSpeed")
    val rlWheelSpeed: Double,

    @SerialName("RR_WheelSpeed")
    val rrWheelSpeed: Double,

    @SerialName("SteeringAngle")
    val steeringAngle: Double,

    @SerialName("SteeringSpeed")
    val steeringSpeed: Int,

    @SerialName("Gear_Lever")
    val gearLever: Int,

    @SerialName("VehSpd")
    val vehSpd: Double,

    @SerialName("YAW_RATE")
    val yawRate: Double,

    @SerialName("LAT_ACCEL")
    val latAccel: Double,

    @SerialName("LONG_ACCEL")
    val longAccel: Double
)
