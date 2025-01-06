package com.notsatria.poms.ui

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.notsatria.poms.ui.components.PomsTimer
import com.notsatria.poms.ui.components.StepIndicator
import com.notsatria.poms.ui.theme.Grey
import com.notsatria.poms.ui.theme.LightGrey
import com.notsatria.poms.ui.theme.PomsTheme
import com.notsatria.poms.ui.theme.Red
import com.notsatria.poms.utils.PomoState
import com.notsatria.poms.utils.formatTimeToMinuteAndSecond
import com.notsatria.poms.utils.formatTimeToMinuteOrSecond
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomsApp(modifier: Modifier = Modifier, viewModel: PomsViewModel = viewModel()) {
    val timerState by viewModel.timerState.collectAsState()
    LaunchedEffect(timerState.isRunning) {
        delay(100L)
        while (timerState.isRunning) {
            delay(100L)
            timerState.tick()
        }
    }
    Scaffold(modifier,
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Pomodoro Timer") }, actions = {
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = "Setting")
                }
            })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            PomsTimer(
                modifier = Modifier
                    .size(300.dp),
                progress = if (!timerState.isRunning && timerState.currentTime == timerState.workTime) 0.001f else timerState.progress,
                timerText = formatTimeToMinuteAndSecond(timerState.currentTime / 1000L),
                progressColor = animateColorAsState(
                    targetValue = timerState.color,
                    label = ""
                ).value
            )
            Spacer(modifier = Modifier.height(16.dp))
            StepIndicator(
                dotCount = timerState.steps,
                currentStep = timerState.currentStep,
                dotColor = timerState.color
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = if (timerState.currentState == PomoState.WORK) {
                    "Stay focus for ${
                        formatTimeToMinuteOrSecond(
                            timerState.workTime
                        )
                    }"
                } else {
                    "Take a break for ${
                        formatTimeToMinuteOrSecond(timerState.breakTime)
                    }"
                },
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                FilledIconButton(
                    modifier = Modifier.size(48.dp),
                    onClick = { timerState.reset() },
                    colors = IconButtonDefaults.filledIconButtonColors()
                        .copy(containerColor = LightGrey, contentColor = Grey)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Restart timer")
                }
                Spacer(modifier = Modifier.width(12.dp))
                FilledIconButton(
                    modifier = Modifier.size(72.dp),
                    onClick = {
                        if (timerState.isRunning) timerState.pause() else timerState.start()
                    },
                    colors = IconButtonDefaults.filledIconButtonColors()
                        .copy(containerColor = Red, contentColor = Color.White)
                ) {
                    Icon(
                        imageVector = if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause timer"
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                FilledIconButton(
                    modifier = Modifier.size(48.dp),
                    onClick = { timerState.stop() },
                    colors = IconButtonDefaults.filledIconButtonColors()
                        .copy(containerColor = LightGrey, contentColor = Grey)
                ) {
                    Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop timer")
                }
            }
        }
    }
}

@Preview
@Composable
fun PomsAppPreview() {
    PomsTheme {
        PomsApp()
    }
}