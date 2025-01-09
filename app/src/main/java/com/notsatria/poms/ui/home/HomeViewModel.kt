package com.notsatria.poms.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.poms.data.preferences.SettingPreference
import com.notsatria.poms.utils.TimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val settingPreference: SettingPreference) :
    ViewModel() {
    private val _timerState =
        MutableStateFlow(
            TimerState()
        )
    val timerState = _timerState.asStateFlow()

    init {
        getPomoSettings()
    }

    private fun getPomoSettings() {
        viewModelScope.launch {
            combine(
                settingPreference.workingTime,
                settingPreference.breakTime,
                settingPreference.workingSession
            ) { workingTime, breakTime, workingSession ->
                _timerState.value =
                    TimerState(
                        (workingTime ?: 25),
                        (breakTime ?: 5),
                        (workingSession ?: 4)
                    )
            }
        }
    }
}