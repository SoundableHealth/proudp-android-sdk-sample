package com.soundable.proudpandroidsample

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.soundable.health.sdk.api.SoundableHealth
import com.soundable.health.sdk.model.Gender
import com.soundable.health.sdk.model.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

@Composable
fun MainScreen() {
    val activity = LocalContext.current as Activity
    val lifecycleOwner = LocalLifecycleOwner.current
    var isAudioPermission by remember {
        mutableStateOf(ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
        isAudioPermission = result
    }
    var isRecording by remember { mutableStateOf(false) }
    var isAnalysis by remember { mutableStateOf(false) }
    var progress by remember { mutableStateOf(0) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    SoundableHealth.cancel()
                    isRecording = false
                    isAnalysis = false
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = toTime(progress),
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Icon(
            imageVector = if (isRecording) ImageVector.vectorResource(R.drawable.stop) else Icons.Default.PlayArrow,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterHorizontally)
                .clickable {
                    if (isAudioPermission) {
                        if (isRecording) {
                            isRecording = false
                            isAnalysis = true
                            SoundableHealth.stop(
                                onError = {
                                    isAnalysis = false
                                    Log.d("[soundable]", it)
                                },
                                onFinish = {
                                    isAnalysis = false
                                    Log.d("[soundable]", "result $it")
                                }
                            )
                        } else {
                            isRecording = true
                            SoundableHealth.start(
                                context = activity,
                                user = User(gender = Gender.MALE, clinic = "soundable"),
                                onError = {
                                    isRecording = false
                                    Log.d("[soundable]", "start $it")
                                },
                                onRecording = {
                                    progress = it
                                },
                            )
                        }
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
        )
        if (isAnalysis) {
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(60.dp),
                    color = Color.Magenta,
                    strokeWidth = 7.dp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Analyzing...",
                    fontSize = 18.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

private fun toTime(second: Int): String {
    val dateFormat = SimpleDateFormat("HH:mm:ss")
    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"))
    return dateFormat.format(Date(second * 1000L))

}

@Preview
@Composable
fun MainScreenPreview() {
    MainScreen()
}