package com.notsatria.poms.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.notsatria.poms.MainActivity
import com.notsatria.poms.R
import com.notsatria.poms.data.preferences.SettingPreference
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PomodoroService : Service() {
    private var timerJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.Default + Job())
    private var timerState = TimerState()
    private var countDownTimer: CountDownTimer? = null

    @Inject
    lateinit var settingPreference: SettingPreference

    override fun onBind(intent: Intent): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            val breakTime = settingPreference.breakTime.first() ?: 5
            val workTime = settingPreference.workingTime.first() ?: 25
            val workingSession = settingPreference.workingSession.first() ?: 4
            timerState = TimerState(workTime, breakTime, workingSession)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Action.START.toString() -> {
                val notification = createNotification(timerState)
                startForegroundService(notification.build())
                startTimer()
            }

            Action.PAUSE.toString() -> pauseTimer()
            Action.STOP.toString() -> stopTimer()
        }

        return START_STICKY
    }

    private fun startForegroundService(notification: Notification) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SHORT_SERVICE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotification(timerState: TimerState): NotificationCompat.Builder {
        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseIntent = PendingIntent.getService(
            this,
            1,
            Intent(this, PomodoroService::class.java).apply { action = Action.PAUSE.toString() },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = PendingIntent.getService(
            this,
            2,
            Intent(this, PomodoroService::class.java).apply { action = Action.STOP.toString() },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = when (timerState.currentState) {
            PomoState.WORK -> "Work Session ${timerState.currentWorkSession}"
            PomoState.BREAK -> "Break Time"
        }

        val minutes = timerState.currentTime / 60000
        val seconds = (timerState.currentTime % 60000) / 1000
        val content = String.format("%02d:%02d remaining", minutes, seconds)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_timer_24dp)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(contentIntent)
            .addAction(R.drawable.ic_pause_24, "Pause", pauseIntent)
            .addAction(R.drawable.ic_stop_24, "Stop", stopIntent)
    }

    fun updateTimer(newState: TimerState) {
        timerState = newState
    }

    private fun startTimer() {
        countDownTimer?.cancel()

        val totalMillis = timerState.currentTime
        countDownTimer = object : CountDownTimer(totalMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerState = timerState.copy().apply {
                    currentTime = millisUntilFinished
                }

                // Update notification
                val notification = createNotification(timerState)
                val notificationManager = getSystemService(NotificationManager::class.java)
                notificationManager.notify(NOTIFICATION_ID, notification.build())

                // Broadcast timer update
                sendTimerBroadcast()
            }

            override fun onFinish() {
                timerState.stop()
                sendTimerBroadcast(isFinished = true)
                stopTimer()
            }
        }.start()

        timerState.start()
    }

    private fun sendTimerBroadcast(isFinished: Boolean = false) {
        val intent = Intent(if (isFinished) "ACTION_TIMER_FINISH" else "ACTION_TIMER_TICK").apply {
            putExtra("EXTRA_TIMER_STATE", timerState)
            setPackage(packageName)
        }
        sendBroadcast(intent)
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        timerState.pause()
        sendTimerBroadcast()
    }

    private fun stopTimer() {
        countDownTimer?.cancel()
        timerState.stop()
        sendTimerBroadcast()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        serviceScope.cancel()
    }

    private fun startTimerJob() {
        timerJob?.cancel()
//        timerJob = serviceScope.launch {
//            var progress = 0
//            val maxValue = timerState.workTimeMinutes * 60
//            while (timerState.isRunning) {
//                delay(100L)
//                timerState.tick()
//                progress++
//                updateNotification(maxValue, progress)
//            }
//        }
        object : CountDownTimer((timerState.workTimeMinutes * 60).toLong(), 1000) {
            var progress = 0
            override fun onTick(millisBeforeFinished: Long) {
                progress++
                updateNotification((timerState.workTimeMinutes * 60), progress)
                updateTimer(timerState)
                val intent = Intent("TIMER_STATE").apply {
                    putExtra("timer_state", timerState)
                    setPackage(packageName)
                }
                sendBroadcast(intent)
            }

            override fun onFinish() {
                progress = 0
                stopTimer()
                updateTimer(timerState)
            }
        }.start()
    }

    private fun updateNotification(maxValue: Int, progress: Int) {
        val notification = createNotification(timerState)
        notification.setProgress(maxValue, progress, false)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    enum class Action {
        START,
        STOP,
        PAUSE
    }
}