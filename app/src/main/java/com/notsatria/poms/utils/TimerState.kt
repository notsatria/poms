package com.notsatria.poms.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class TimerState(
    private val totalTime: Long,
    private val tickInterval: Long = 100L
) {
    var currentTime by mutableLongStateOf(totalTime)
        private set

    var isRunning by mutableStateOf(false)
        private set

    val progress: Float
        get() = (totalTime - currentTime).toFloat() / totalTime

    fun start() {
        if (currentTime <= 0L) reset()
        isRunning = true
    }

    fun pause() {
        isRunning = false
    }

    fun stop() {
        currentTime = 0L
        isRunning = false
    }

    fun reset() {
        currentTime = totalTime
        isRunning = false
    }

    fun tick() {
        if (isRunning && currentTime > 0L) {
            currentTime -= tickInterval
        }
    }
}