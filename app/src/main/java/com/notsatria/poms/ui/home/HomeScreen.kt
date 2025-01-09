package com.notsatria.poms.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notsatria.poms.ui.components.PomsTimer
import com.notsatria.poms.ui.components.StepIndicator
import com.notsatria.poms.ui.theme.Grey
import com.notsatria.poms.ui.theme.LightGrey
import com.notsatria.poms.ui.theme.PomsTheme
import com.notsatria.poms.ui.theme.Red
import com.notsatria.poms.utils.PomoState
import com.notsatria.poms.utils.TimerState
import com.notsatria.poms.utils.formatTimeToMinuteAndSecond
import com.notsatria.poms.utils.formatTimeToMinuteOrSecond
import com.notsatria.poms.utils.minutesToMillis
import kotlinx.coroutines.delay

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToSettingScreen: () -> Unit
) {
    val timerState by viewModel.timerState.collectAsState()
    LaunchedEffect(timerState.isRunning) {
        delay(100L)
        while (timerState.isRunning) {
            delay(100L)
            timerState.tick()
        }
    }
    HomeScreen(modifier = modifier, uiState = HomeUiState(timerState), navigateToSettingScreen)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier, uiState: HomeUiState, navigateToSettingScreen: () -> Unit) {
    Scaffold(modifier, topBar = {
        CenterAlignedTopAppBar(title = { Text(text = "Pomodoro Timer") }, actions = {
            IconButton(onClick = navigateToSettingScreen) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Setting")
            }
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            PomsTimer(
                modifier = Modifier.size(300.dp),
                progress = if (!uiState.timerState.isRunning && uiState.timerState.currentTime == uiState.timerState.workTimeMinutes.minutesToMillis()) 0.001f else uiState.timerState.progress,
                timerText = formatTimeToMinuteAndSecond(uiState.timerState.currentTime / 1000L),
                progressColor = animateColorAsState(
                    targetValue = uiState.timerState.color, label = ""
                ).value
            )
            Spacer(modifier = Modifier.height(16.dp))
            StepIndicator(
                dotCount = uiState.timerState.workingSession,
                currentStep = uiState.timerState.currentWorkSession,
                dotColor = uiState.timerState.color
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = if (uiState.timerState.currentState == PomoState.WORK) {
                    "Stay focus for ${
                        formatTimeToMinuteOrSecond(
                            uiState.timerState.workTimeMinutes.minutesToMillis()
                        )
                    }"
                } else {
                    "Take a break for ${
                        formatTimeToMinuteOrSecond(uiState.timerState.breakTimeMinutes.minutesToMillis())
                    }"
                }, style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                FilledIconButton(
                    modifier = Modifier.size(48.dp),
                    onClick = { uiState.timerState.reset() },
                    colors = IconButtonDefaults.filledIconButtonColors()
                        .copy(containerColor = LightGrey, contentColor = Grey)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Restart timer")
                }
                Spacer(modifier = Modifier.width(12.dp))
                FilledIconButton(
                    modifier = Modifier.size(72.dp),
                    onClick = {
                        if (uiState.timerState.isRunning) uiState.timerState.pause() else uiState.timerState.start()
                    },
                    colors = IconButtonDefaults.filledIconButtonColors()
                        .copy(containerColor = Red, contentColor = Color.White)
                ) {
                    Icon(
                        imageVector = if (uiState.timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause timer"
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                FilledIconButton(
                    modifier = Modifier.size(48.dp),
                    onClick = { uiState.timerState.stop() },
                    colors = IconButtonDefaults.filledIconButtonColors()
                        .copy(containerColor = LightGrey, contentColor = Grey)
                ) {
                    Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop timer")
                }
            }
        }
    }
}

data class HomeUiState(
    val timerState: TimerState,
)

@Preview
@Composable
fun HomeScreenPreview() {
    PomsTheme {
        HomeScreen(Modifier, HomeUiState(
            TimerState(
                workTimeMinutes = 25,
                breakTimeMinutes = 5,
                workingSession = 4
            )
        ), navigateToSettingScreen = {})
    }
}