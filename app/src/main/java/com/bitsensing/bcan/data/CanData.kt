package com.bitsensing.bcan.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CanData(
    val timestamp: Float,
    val canId: Int,
    val isExtendedId: Boolean,
    val dlc: Int,
    val isFd: Boolean,
    val name: String,
    val signals: Map<String, Float>,   // adjust type if you know signal values are numeric
    val raw: String
)

fun DecodedData.toMap(): Map<String, Any> = mapOf(
    "FL_WheelSpeed" to flWheelSpeed,
    "FR_WheelSpeed" to frWheelSpeed,
    "RL_WheelSpeed" to rlWheelSpeed,
    "RR_WheelSpeed" to rrWheelSpeed,
    "SteeringAngle" to steeringAngle,
    "SteeringSpeed" to steeringSpeed,
    "Gear_Lever" to gearLever,
    "VehSpd" to vehSpd,
    "YAW_RATE" to yawRate,
    "LAT_ACCEL" to latAccel,
    "LONG_ACCEL" to longAccel
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
