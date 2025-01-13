package com.notsatria.poms.ui.settings

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Label
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.notsatria.poms.ui.components.TimeTextField
import com.notsatria.poms.ui.theme.PomsTheme
import timber.log.Timber.Forest.i
import kotlin.math.roundToInt

@Composable
fun PomsSettingRoute(navigateBack: () -> Unit, viewModel: SettingViewModel = hiltViewModel()) {
    val timerState by viewModel.timerState.collectAsState()
    i("Timerstate: ${timerState.workTimeMinutes}, ${timerState.breakTimeMinutes}, ${timerState.workingSession}")
    val isUpdated by viewModel.isUpdated.collectAsState()
    var workingSessionSliderPosition by remember {
        mutableFloatStateOf(timerState.workingSession.toFloat())
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    LaunchedEffect(key1 = isUpdated) {
        if (isUpdated) navigateBack()
    }
    SettingScreen(
        uiState = SettingUiState(
            sliderPosition = timerState.workingSession.toFloat(),
            interactionSource = interactionSource,
            workingTime = timerState.workTimeMinutes,
            breakTime = timerState.breakTimeMinutes
        ),
        navigateBack = navigateBack,
        onSliderPositionChange = {
            workingSessionSliderPosition = it
            viewModel.updateWorkingSessionState(it.toInt())
        },
        onWorkingTimeChange = { viewModel.updateWorkTimeState(if (it.isEmpty()) 1 else it.toInt()) },
        onBreakTimeChange = { viewModel.updateBreakTimeState(if (it.isEmpty()) 1 else it.toInt()) },
        onSaveClicked = { viewModel.updateSettings() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    uiState: SettingUiState,
    navigateBack: () -> Unit = {},
    onSliderPositionChange: (Float) -> Unit = {},
    onWorkingTimeChange: (String) -> Unit = {},
    onBreakTimeChange: (String) -> Unit = {},
    onSaveClicked: () -> Unit = {}
) {
    Scaffold(modifier, topBar = {
        TopAppBar(title = { Text(text = "Set Pomodoro") }, navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        })
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Row {
                TimeTextField(
                    modifier = Modifier.weight(1f),
                    labelText = "Working Time",
                    value = uiState.workingTime.toString(),
                    onValueChange = onWorkingTimeChange,
                )
                Spacer(modifier = Modifier.width(8.dp))
                TimeTextField(
                    modifier = Modifier.weight(1f),
                    labelText = "Break Time",
                    value = uiState.breakTime.toString(),
                    onValueChange = onBreakTimeChange,
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text(text = "Working Sessions")
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = uiState.sliderPosition,
                onValueChange = onSliderPositionChange,
                valueRange = 1f..8f,
                steps = 6,
                interactionSource = uiState.interactionSource,
                thumb = {
                    Label(
                        label = {
                            PlainTooltip(
                                modifier = Modifier
                                    .sizeIn(25.dp, 25.dp)
                                    .wrapContentWidth(),
                                containerColor = SliderDefaults.colors().inactiveTrackColor,
                                contentColor = SliderDefaults.colors().activeTrackColor
                            ) {
                                Text(uiState.sliderPosition.roundToInt().toString())
                            }
                        },
                        interactionSource = uiState.interactionSource,
                        isPersistent = false
                    ) {
                        SliderDefaults.Thumb(
                            interactionSource = uiState.interactionSource,
                            colors = SliderDefaults.colors()
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(modifier = Modifier.fillMaxWidth(), onClick = onSaveClicked) {
                Text(text = "Save changes")
            }
        }
    }
}

data class SettingUiState(
    val sliderPosition: Float = 1f,
    val interactionSource: MutableInteractionSource = MutableInteractionSource(),
    val workingTime: Int,
    val breakTime: Int
)

@Preview
@Composable
fun PomsSettingScreenPreview() {
    PomsTheme {
        SettingScreen(
            uiState = SettingUiState(workingTime = 25, breakTime = 5),
            navigateBack = {},
            onSliderPositionChange = {}
        )
    }
}