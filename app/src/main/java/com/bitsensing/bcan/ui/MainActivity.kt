package com.bitsensing.bcan.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.OutlinedTextField
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bitsensing.bcan.repository.CanRepository
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch

class CanViewModel : ViewModel() {
    var ipAddress by mutableStateOf("")
    var frames = mutableStateListOf<String>()
    var isLoading by mutableStateOf(false)

    private fun isValidIp(ip: String): Boolean {
        val regex = Regex("^\\d{1,3}(\\.\\d{1,3}){3}$")
        return regex.matches(ip) && ip.split(".").all { it.toInt() in 0..255 }
    }

    fun loadFrames() {
        if (!isValidIp(ipAddress)) return
        val repo = CanRepository(ipAddress)
        viewModelScope.launch {
            isLoading = true
            val response = repo.fetchCanFrames()
            if (response.isSuccessful) {
                frames.clear()
                frames.addAll(response.body()?.frames?.map { "${it.name}: ${it.decoded}" } ?: listOf())
            }
            isLoading = false
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: CanViewModel = viewModel()
            MaterialTheme {
                Column {
                    OutlinedTextField(
                        value = viewModel.ipAddress,
                        onValueChange = { viewModel.ipAddress = it },
                        label = { Text("PC IP Address") }
                    )
                    Button(onClick = { viewModel.loadFrames() }) {
                        Text("Fetch CAN Data")
                    }
                    if (viewModel.isLoading) {
                        CircularProgressIndicator()
                    } else {
                        LazyColumn {
                            items(viewModel.frames) {
                                Text(it)
                            }
                        }
                    }
                }
            }
        }
    }
}
