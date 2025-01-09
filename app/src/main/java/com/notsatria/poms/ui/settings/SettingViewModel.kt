package com.notsatria.poms.ui.settings

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
class SettingViewModel @Inject constructor(private val settingPreference: SettingPreference) :
    ViewModel() {
    private val _timerState =
        MutableStateFlow(TimerState())
    val timerState = _timerState.asStateFlow()

    init {
        getDefaultSetting()
    }

    private fun getDefaultSetting() {
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

    fun updateWorkTimeState(time: Int) {
        _timerState.value.workTimeMinutes = time
    }

    fun updateBreakTimeState(time: Int) {
        _timerState.value.breakTimeMinutes = time
    }

    fun updateWorkingSessionState(session: Int) {
        _timerState.value.workingSession = session
    }

    fun updateSettings() {
        viewModelScope.launch {
            settingPreference.saveWorkingTime(_timerState.value.workTimeMinutes)
            settingPreference.saveBreakTime(_timerState.value.breakTimeMinutes)
            settingPreference.saveWorkingTime(_timerState.value.workingSession)
        }
    }
}