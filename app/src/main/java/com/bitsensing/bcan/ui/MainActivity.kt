package com.bitsensing.bcan.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
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
import kotlinx.coroutines.launch

class CanViewModel : ViewModel() {
    private val repo = CanRepository()
    var frames = mutableListOf<String>()
    var isLoading = false

    fun loadFrames() {
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
