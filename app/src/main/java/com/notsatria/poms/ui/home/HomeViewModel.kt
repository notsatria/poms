package com.notsatria.poms.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notsatria.poms.data.preferences.SettingPreference
import com.notsatria.poms.utils.TimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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

    fun getPomoSettings() {
        viewModelScope.launch {
            val breakTime = settingPreference.breakTime.first() ?: 5
            val workTime = settingPreference.workingTime.first() ?: 25
            val workingSession = settingPreference.workingSession.first() ?: 4
            _timerState.value = TimerState(workTime, breakTime, workingSession)
        }
    }
}