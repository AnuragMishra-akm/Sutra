package com.example.sutra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import com.example.sutra.navigation.NavGraph
import com.example.sutra.ui.theme.SutraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Coil 3 with a Networker to enable loading images from URLs
        SingletonImageLoader.setSafe { context ->
            coil3.ImageLoader.Builder(context)
                .components {
                    add(OkHttpNetworkFetcherFactory())
                }
                .build()
        }

        enableEdgeToEdge()
        setContent {
            SutraTheme {
                val navController = rememberNavController()
                NavGraph(navController)
            }
        }
    }
}
