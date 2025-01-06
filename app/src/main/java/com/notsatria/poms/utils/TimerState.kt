package com.notsatria.poms.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.notsatria.poms.ui.theme.Blue
import com.notsatria.poms.ui.theme.Red

class TimerState(
    val workTime: Long = 25 * 60 * 1000,
    val breakTime: Long = 30 * 1000L,
    private val tickInterval: Long = 100L
) {
    var currentTime by mutableLongStateOf(workTime)
        private set

    var isRunning by mutableStateOf(false)
        private set

    var currentState by mutableStateOf(PomoState.WORK)
        private set

    val steps = 3

    var currentStep by mutableIntStateOf(0)
        private set

    var color by mutableStateOf(Red)
        private set

    val progress: Float
        get() = when (currentState) {
            PomoState.WORK -> (workTime - currentTime).toFloat() / workTime
            PomoState.BREAK -> (breakTime - currentTime).toFloat() / breakTime
        }

    fun start() {
        if (currentTime <= 0L) reset()
        isRunning = true
    }

    fun pause() {
        isRunning = false
    }

    fun stop() {
        isRunning = false
        currentTime = 0L
        switchState()
    }

    fun reset() {
        currentTime = if (currentState == PomoState.WORK) workTime else breakTime
        isRunning = false
    }

    fun tick() {
        if (isRunning) {
            if (currentTime > 0L) {
                currentTime -= tickInterval
            } else {
                if (currentStep >= steps && currentState == PomoState.WORK) resetAllOnStepDone()
                else switchState()
            }
        }
    }

    private fun resetAllOnStepDone() {
        currentState = PomoState.WORK
        currentStep = 0
        isRunning = false
        currentTime = workTime
        color = Red
    }

    private fun switchState() {
        currentState = if (currentState == PomoState.WORK) PomoState.BREAK else PomoState.WORK
        currentTime = if (currentState == PomoState.WORK) workTime else breakTime
        color = if (currentState == PomoState.WORK) Red else Blue

        if (currentState == PomoState.WORK) addStep()
    }

    private fun addStep() {
        if (currentStep < steps)
            currentStep += 1
    }
}

enum class PomoState {
    WORK, BREAK
}
