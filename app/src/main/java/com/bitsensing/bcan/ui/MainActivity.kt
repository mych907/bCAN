package com.bitsensing.bcan.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.bitsensing.bcan.ui.theme.bCANTheme

//class CanViewModel : ViewModel() {
//    var ipAddress by mutableStateOf("")
//    var frames = mutableStateListOf<String>()
//    var isLoading by mutableStateOf(false)
//
//    private fun isValidIp(ip: String): Boolean {
//        val regex = Regex("^\\d{1,3}(\\.\\d{1,3}){3}$")
//        return regex.matches(ip) && ip.split(".").all { it.toInt() in 0..255 }
//    }
//
//    fun loadFrames() {
//        if (!isValidIp(ipAddress)) return
//        val repo = CanRepository(ipAddress)
//        viewModelScope.launch {
//            isLoading = true
//            val response = repo.fetchCanFrames()
//            if (response.isSuccessful) {
//                frames.clear()
//                frames.addAll(response.body()?.frames?.map { "${it.name}: ${it.decoded}" } ?: listOf())
//            }
//            isLoading = false
//        }
//    }
//}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {

    // val viewModel: CanViewModel = viewModel()
    Surface(modifier) {
        Column {
            Button(onClick = { /* TODO */ }) {
                Text("Fetch CAN Data")
            }
            Button(onClick = { /* TODO */ }) {
                Text("Fetch CAN Data")
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(Modifier.fillMaxSize())
}
