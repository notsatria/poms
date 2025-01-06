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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Label
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notsatria.poms.ui.components.TimeTextField
import com.notsatria.poms.ui.theme.PomsTheme
import kotlin.math.roundToInt

@Composable
fun PomsSettingRoute() {
    val workingSessionSliderPosition by remember {
        mutableFloatStateOf(1f)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    PomsSettingScreen(
        uiState = PomsSettingUiState(
            sliderPosition = workingSessionSliderPosition,
            interactionSource = interactionSource
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomsSettingScreen(modifier: Modifier = Modifier, uiState: PomsSettingUiState) {
    Scaffold(modifier, topBar = {
        TopAppBar(title = { Text(text = "Set Pomodoro") })
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
                    value = "25",
                    onValueChange = { },
                )
                Spacer(modifier = Modifier.width(8.dp))
                TimeTextField(
                    modifier = Modifier.weight(1f),
                    labelText = "Break Time",
                    value = "5",
                    onValueChange = { },
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            Text(text = "Working Sessions")
            Spacer(modifier = Modifier.height(8.dp))
            Slider(
                value = uiState.sliderPosition,
                onValueChange = { uiState.sliderPosition = it },
                valueRange = 1f..8f,
                steps = 6,
                interactionSource = uiState.interactionSource,
                onValueChangeFinished = {

                },
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
            Button(modifier = Modifier.fillMaxWidth(), onClick = { }) {
                Text(text = "Save changes")
            }
        }
    }
}

data class PomsSettingUiState(
    var sliderPosition: Float = 1f,
    val interactionSource: MutableInteractionSource = MutableInteractionSource(),
)

@Preview
@Composable
fun PomsSettingScreenPreview() {
    PomsTheme {
        PomsSettingScreen(uiState = PomsSettingUiState())
    }
}