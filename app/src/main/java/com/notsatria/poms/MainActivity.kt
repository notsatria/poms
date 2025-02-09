package com.notsatria.poms

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.notsatria.poms.ui.PomsApp
import com.notsatria.poms.ui.theme.PomsTheme
import com.notsatria.poms.utils.CHANNEL_ID
import com.notsatria.poms.utils.CHANNEL_NAME
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
               // empty
            } else {
                handlePermissions()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handlePermissions()
        setContent {
            PomsTheme {
                PomsApp()
            }
        }
    }

    private fun handlePermissions() {
        when {
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(
                    this@MainActivity,
                    "Notification permission granted",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}