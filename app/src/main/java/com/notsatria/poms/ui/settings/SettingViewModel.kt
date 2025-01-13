package com.notsatria.poms.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.poms.data.preferences.SettingPreference
import com.notsatria.poms.utils.TimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber.Forest.i
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val settingPreference: SettingPreference) :
    ViewModel() {
    private val _timerState = MutableStateFlow(TimerState())
    val timerState = _timerState.asStateFlow()

    private val _isUpdated = MutableStateFlow(false)

    /**
     * This variable will trigger the UI to navigate back if true
     */
    val isUpdated = _isUpdated.asStateFlow()

    init {
        getDefaultSetting()
    }

    private fun getDefaultSetting() {
        viewModelScope.launch {
            val breakTime = settingPreference.breakTime.first() ?: 5
            val workTime = settingPreference.workingTime.first() ?: 25
            val workingSession = settingPreference.workingSession.first() ?: 4
            _timerState.value = TimerState(workTime, breakTime, workingSession)
        }
    }

    fun updateWorkTimeState(time: Int) {
        _timerState.value = _timerState.value.copy(workTimeMinutes = time)
        i("Timer state workTimeMinutes: ${_timerState.value.workTimeMinutes}")
    }

    fun updateBreakTimeState(time: Int) {
        _timerState.value = _timerState.value.copy(breakTimeMinutes = time)
        i("Timer state breakTimeMinutes: ${_timerState.value.breakTimeMinutes}")
    }

    fun updateWorkingSessionState(session: Int) {
        _timerState.value = _timerState.value.copy(workingSession = session)
        i("Timer state workingSession: ${_timerState.value.workingSession}")
    }

    fun updateSettings() {
        viewModelScope.launch {
            val state = _timerState
            settingPreference.saveWorkingTime(state.value.workTimeMinutes)
            settingPreference.saveBreakTime(state.value.breakTimeMinutes)
            settingPreference.saveWorkingSession(state.value.workingSession)
            _isUpdated.value = true
        }
    }
}