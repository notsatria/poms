package com.notsatria.poms.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TimerState(
    private val workTime: Long,
    private val breakTime: Long,
    private val tickInterval: Long = 100L
) {
    var currentTime by mutableLongStateOf(workTime)
        private set

    var isRunning by mutableStateOf(false)
        private set

    var currentState by mutableStateOf(PomoState.WORK)
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
        currentState = PomoState.WORK
        currentTime = workTime
        isRunning = false
    }

    fun tick() {
        if (isRunning) {
            if (currentTime > 0L) {
                currentTime -= tickInterval
            } else {
                switchState()
            }
        }
    }

    private fun switchState() {
        currentState = if (currentState == PomoState.WORK) PomoState.BREAK else PomoState.WORK
        currentTime = if (currentState == PomoState.WORK) workTime else breakTime
    }
}

enum class PomoState {
    WORK, BREAK
}
