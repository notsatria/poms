package com.notsatria.poms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.notsatria.poms.ui.PomsApp
import com.notsatria.poms.ui.theme.PomsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PomsTheme {
               PomsApp()
            }
        }
    }
}