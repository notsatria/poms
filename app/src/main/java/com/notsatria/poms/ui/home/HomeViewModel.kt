package com.notsatria.poms.ui.home

import androidx.lifecycle.ViewModel
import com.notsatria.poms.utils.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _timerState =
        MutableStateFlow(TimerState(workTime = 6 * 1000L, breakTime = 3 * 1000L))
    val timerState = _timerState.asStateFlow()
}