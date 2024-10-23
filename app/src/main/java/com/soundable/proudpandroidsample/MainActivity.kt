package com.soundable.proudpandroidsample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.soundable.health.sdk.api.SoundableHealth
import com.soundable.proudpandroidsample.ui.theme.ProudpandroidsampleTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ProudpandroidsampleTheme {
                MainScreen()
            }
        }

        // Contact to dev@soundable.health to get the server configuration
        SoundableHealth.init(
            apiKey = "apiKey",
            serverUrl = "serverUrl",
            socketUrl = "socketUrl",
        )
    }

}