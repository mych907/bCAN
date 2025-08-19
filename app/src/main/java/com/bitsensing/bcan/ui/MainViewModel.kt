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
import kotlin.collections.listOf

sealed interface CanUiState {
    data class Success(
        val canDataList: List<CanData>,
        val isRefreshing: Boolean = false
    ) : CanUiState
    object Error : CanUiState
    object Loading : CanUiState
}

class MainViewModel : ViewModel() {
    var canUiState: CanUiState by mutableStateOf(CanUiState.Loading)
        private set

    private var cache: List<CanData> = emptyList()

    init { startAutoRefresh() }

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
            val hadData = canUiState is CanUiState.Success
            if (hadData) {
                val s = canUiState as CanUiState.Success
                canUiState = s.copy(isRefreshing = true)   // keep showing list
            } else {
                canUiState = CanUiState.Loading            // only before first data
            }

            canUiState = try {
                val response = CanApi.retrofitService.getCanFrames()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        // Map<String, CanData> -> List<CanData> (inject canId)
                        val freshList = body.map { (id, data) ->
                            data.copy(canId = id.toInt())
                        }
                        // Optional: keep a stable order
                        val replaced = freshList.sortedBy { it.canId }

                        cache = replaced
                        CanUiState.Success(replaced, isRefreshing = false)
                    } else {
                        if (cache.isNotEmpty()) CanUiState.Success(cache, isRefreshing = false)
                        else CanUiState.Error
                    }
                } else {
                    if (cache.isNotEmpty()) CanUiState.Success(cache, isRefreshing = false)
                    else CanUiState.Error
                }
            } catch (e: IOException) {
                Log.e("MainViewModel", "Network issue.", e)
                if (cache.isNotEmpty()) CanUiState.Success(cache, isRefreshing = false)
                else CanUiState.Error
            } catch (e: Exception) {
                Log.e("MainViewModel", "Unexpected error.", e)
                if (cache.isNotEmpty()) CanUiState.Success(cache, isRefreshing = false)
                else CanUiState.Error
            }
        }
    }
}