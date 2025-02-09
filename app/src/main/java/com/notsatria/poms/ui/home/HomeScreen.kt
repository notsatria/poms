package com.notsatria.poms.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import com.notsatria.poms.R
import com.notsatria.poms.ui.components.PomsTimer
import com.notsatria.poms.ui.components.StepIndicator
import com.notsatria.poms.ui.theme.Grey
import com.notsatria.poms.ui.theme.LightGrey
import com.notsatria.poms.ui.theme.PomsTheme
import com.notsatria.poms.ui.theme.Red
import com.notsatria.poms.utils.CHANNEL_ID
import com.notsatria.poms.utils.PomoState
import com.notsatria.poms.utils.PomodoroService
import com.notsatria.poms.utils.TimerState
import com.notsatria.poms.utils.formatTimeToMinuteAndSecond
import com.notsatria.poms.utils.formatTimeToMinuteOrSecond
import com.notsatria.poms.utils.minutesToMillis
import timber.log.Timber.Forest.d

@Composable
fun HomeRoute(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToSettingScreen: () -> Unit
) {
    val context = LocalContext.current
    val timerState by viewModel.timerState.collectAsState()
    val countdownReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, intent: Intent?) {
                when (intent?.action) {
                    "ACTION_TIMER_TICK" -> {
                        val newState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            intent.getParcelableExtra("EXTRA_TIMER_STATE", TimerState::class.java)!!
                        } else {
                            @Suppress("DEPRECATION")
                            intent.getParcelableExtra("EXTRA_TIMER_STATE")!!
                        }
                        d("Timer state: $timerState")

                        newState.let { viewModel.updateTimerState(it) }
                    }
                    "ACTION_TIMER_FINISH" -> {

                    }
                }

            }
        }
    }
    val intentFilter = IntentFilter().apply {
        addAction("ACTION_TIMER_TICK")
        addAction("ACTION_TIMER_FINISH")
    }

    LifecycleStartEffect(true) {
        ContextCompat.registerReceiver(
            context,
            countdownReceiver,
            intentFilter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        onStopOrDispose { context.unregisterReceiver(countdownReceiver) }
    }

    val radius = if (!timerState.isRunning) {
        36.dp
    } else {
        12.dp
    }
    val playButtonCornerRadius = animateDpAsState(targetValue = radius, label = "")
    HomeScreen(
        modifier = modifier,
        uiState = HomeUiState(timerState, playButtonCornerRadius.value),
        navigateToSettingScreen,
        onPlayClicked = {
            if (timerState.isRunning) {
                viewModel.handleAction(TimerAction.Pause)
                startPomodoroService(context, PomodoroService.Action.PAUSE)
            } else {
                startPomodoroService(context, PomodoroService.Action.START)
            }
        },
        onResetClicked = {
            viewModel.handleAction(TimerAction.Reset)
            startPomodoroService(context, PomodoroService.Action.STOP)
        },
        onStopClicked = {
            viewModel.handleAction(TimerAction.Stop)
            startPomodoroService(context, PomodoroService.Action.STOP)
        }
    )
}

private fun startPomodoroService(context: Context, action: PomodoroService.Action) {
    val intent = Intent(context, PomodoroService::class.java).apply {
        this.action = action.toString()
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(context, intent)
    } else {
        context.startService(intent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier,
    uiState: HomeUiState,
    navigateToSettingScreen: () -> Unit,
    onPlayClicked: () -> Unit,
    onResetClicked: () -> Unit,
    onStopClicked: () -> Unit
) {
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
                ).value,
                isBackwardAnimation = !uiState.timerState.isRunning
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
                    onClick = onResetClicked,
                    colors = IconButtonDefaults.filledIconButtonColors()
                        .copy(containerColor = LightGrey, contentColor = Grey)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Restart timer")
                }
                Spacer(modifier = Modifier.width(12.dp))
                FilledIconButton(
                    modifier = Modifier.size(72.dp),
                    onClick = onPlayClicked,
                    colors = IconButtonDefaults.filledIconButtonColors()
                        .copy(containerColor = Red, contentColor = Color.White),
                    shape = RoundedCornerShape(uiState.playButtonCornerRadius)
                ) {
                    Icon(
                        imageVector = if (uiState.timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause timer"
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                FilledIconButton(
                    modifier = Modifier.size(48.dp),
                    onClick = onStopClicked,
                    colors = IconButtonDefaults.filledIconButtonColors()
                        .copy(containerColor = LightGrey, contentColor = Grey)
                ) {
                    Icon(imageVector = Icons.Default.Stop, contentDescription = "Stop timer")
                }
            }
        }
    }
}

private fun notificationBuilder(context: Context, title: String, content: String) =
    NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_timer_24dp)
        .setContentTitle(title)
        .setContentText(content)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setOngoing(true)

data class HomeUiState(
    val timerState: TimerState,
    val playButtonCornerRadius: Dp,
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
            ),
            20.dp,
        ), navigateToSettingScreen = {},
            onPlayClicked = {},
            onResetClicked = {},
            onStopClicked = {}
        )
    }
}