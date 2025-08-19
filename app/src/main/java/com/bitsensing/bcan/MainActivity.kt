package com.bitsensing.bcan

import android.os.Bundle
import androidx.lifecycle.viewmodel.compose.viewModel
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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.bitsensing.bcan.data.CanData
import com.bitsensing.bcan.data.DecodedData
import com.bitsensing.bcan.ui.CanUiState
import com.bitsensing.bcan.ui.MainViewModel
import com.bitsensing.bcan.ui.theme.bCANTheme
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

fun DecodedData.toMap(): Map<String, Any?> {
    val json = Json.encodeToJsonElement(DecodedData.serializer(), this).jsonObject
    return json.mapValues { it.value.toString() }
}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel()
) {
    val uiState = mainViewModel.canUiState
    val list = (uiState as? CanUiState.Success)?.canDataList.orEmpty()
    val isRefreshing = (uiState as? CanUiState.Success)?.isRefreshing == true
    val listState = rememberLazyListState()

    Box(Modifier.fillMaxSize().padding(top = 80.dp)) {
        // List stays pinned; no vertical re-centering
        LazyColumn(
            state = listState,
            modifier = modifier
                .fillMaxSize()
                .padding(top = 4.dp, bottom = 80.dp) // keep stable padding if you need it
        ) {
            // Use a real stable key; if you have a frame counter or timestamp, prefer that.
            items(list, key = { it.canId /* or it.timestamp.toBits() */ }) { canData ->
                CanMessagePanel(canData = canData)
            }
        }

        // Overlay a thin, fixed-height progress “bar” with alpha instead of show/hide.
        // No layout height change → no bounce.
        Box(
            Modifier
                .fillMaxWidth()
                .height(4.dp)
                .align(Alignment.TopCenter)
                .alpha(if (isRefreshing) 1f else 0f)
        ) {
            LinearProgressIndicator(Modifier.fillMaxSize())
        }

        // Keep the button fixed; not centered with content height.
        Button(
            onClick = { mainViewModel.getCanData() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Text("Refresh")
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            bCANTheme {
                MainScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

//@Preview
@Composable
fun MainScreenPreview() {
    MainScreen(Modifier.fillMaxSize())
}

//@Composable
//private fun CanMessagePanels(
//    modifier: Modifier = Modifier,
//    names: List<String> = List(30) { "$it" }
//) {
//    LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
//        items(items = names) { name ->
//            CanMessagePanel(name = name)
//        }
//    }
//}

@Composable
private fun CanMessagePanel(canData: CanData, modifier: Modifier = Modifier) {

    var expanded by rememberSaveable { mutableStateOf(false) }

    val id = canData.canId
    val idHex = Integer.toHexString(id)
    val rawData = canData.raw
    val formattedRawData = rawData.chunked(2).joinToString( " ")
    val decodedData = canData.signals

    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(vertical = 4.dp, horizontal = 8.dp).clip(RoundedCornerShape(16.dp))
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        ){
            Column(modifier = Modifier
                .weight(1f)
                .padding(12.dp)
            ) {
                Text(text = "$id")
                Text(text = "0x$idHex", style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight =  FontWeight.ExtraBold
                )
                )
                if (expanded) {
                    Text(formattedRawData, modifier = Modifier.padding(10.dp))
                    for ((key, value) in decodedData.toMap()) {
                        Text("$key: $value")
                    }
                }
            }
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.padding(top = 0.dp)
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) {
                        stringResource((R.string.show_less))
                    } else {
                        stringResource((R.string.show_more))
                    }
                )
            }
        }
    }
}

//@Composable
//private fun CanMessagePanels(
//    modifier: Modifier = Modifier,
//    names: List<String> = List(30) { "temp" }
//) {
//    val dummyCanDataList: List<CanData> = listOf(
//        CanData(
//            id = 1104,
//            rawData = "12345678",
//            decodedData = DecodedData(
//                1.0,
//                1.0,
//                1.0,
//                1.0,
//                1.0,
//                1,
//                1,
//                1.0,
//                1.0,
//                1.0,
//                1.0
//            )
//        ),
//        CanData(
//            id = 1204,
//            rawData = "12345678",
//            decodedData = DecodedData(
//                1.0,
//                1.0,
//                1.0,
//                1.0,
//                1.0,
//                1,
//                1,
//                1.0,
//                1.0,
//                1.0,
//                1.0
//            )
//        )
//    )
//
//    LazyColumn(modifier = modifier.padding(vertical = 4.dp)) {
//        items(items = dummyCanDataList) { dummyCanData ->
//            CanMessagePanel(canData = dummyCanData)
//        }
//    }
//}

//@Preview(showBackground = true)
//@Composable
//fun CanMessagePanelPreview() {
//    bCANTheme {
//        CanMessagePanels()
//    }
//}