package com.notsatria.poms.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.notsatria.poms.ui.theme.Blue
import com.notsatria.poms.ui.theme.Red

class TimerState(
    var workTimeMinutes: Int = 25,
    var breakTimeMinutes: Int = 5,
    var workingSession: Int = 4,
    private val tickInterval: Long = 100L
) {
    private val workTime: Long = workTimeMinutes.minutesToMillis()
    private val breakTime: Long = breakTimeMinutes.minutesToMillis()

    var currentTime by mutableLongStateOf(workTime)
        private set

    var isRunning by mutableStateOf(false)
        private set

    var currentState by mutableStateOf(PomoState.WORK)
        private set

    var currentWorkSession by mutableIntStateOf(1)
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
                if (currentWorkSession >= workingSession && currentState == PomoState.WORK) resetAllOnStepDone()
                else switchState()
            }
        }
    }

    private fun resetAllOnStepDone() {
        currentState = PomoState.WORK
        currentWorkSession = 1
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
        if (currentWorkSession < workingSession)
            currentWorkSession += 1
    }
}

enum class PomoState {
    WORK, BREAK
}
