package com.bitsensing.bcan.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitsensing.bcan.data.CanData
import com.bitsensing.bcan.network.CanApi
import kotlinx.coroutines.launch
import java.io.IOException
import android.util.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

sealed interface CanUiState {
    data class Success(val canData: CanData) : CanUiState
    object Error : CanUiState
    object Loading : CanUiState
}

class MainViewModel : ViewModel() {
    var canUiState: CanUiState by mutableStateOf(CanUiState.Loading)
        private set

    init {
        startAutoRefresh()
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (isActive) {
                getCanData()
                delay(1000)
            }
        }
    }

    fun getCanData() {
        viewModelScope.launch {
            canUiState = CanUiState.Loading
            canUiState = try {
                val response = CanApi.retrofitService.getCanFrames() // Store the response

                // Check if the request was successful (HTTP code 2xx)
                if (response.isSuccessful) {
                    // Get the body and ensure it's not null
                    val canData = response.body()
                    if (canData != null) {
                        CanUiState.Success(canData)
                    } else {
                        // Handle the case where the body is null even on a successful response
                        CanUiState.Error
                    }
                } else {
                    // Handle non-successful responses (e.g., 404, 500)
                    CanUiState.Error
                }
            } catch (e: IOException) {
                Log.e("MainViewModel", "IOException, you might have a network issue.", e)
                canUiState = CanUiState.Error
                // Handle exceptions related to network connectivity issues (e.g., no internet)
                CanUiState.Error
            } catch (e: Exception) {
                Log.e("MainViewModel", "An unexpected error occurred.", e)
                canUiState = CanUiState.Error
                CanUiState.Error
            }
        }
    }
}