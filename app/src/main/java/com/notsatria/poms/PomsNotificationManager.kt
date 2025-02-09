package com.notsatria.poms

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.notsatria.poms.utils.CHANNEL_ID
import com.notsatria.poms.utils.NOTIFICATION_ID
import com.notsatria.poms.utils.PomoState
import com.notsatria.poms.utils.TimerState

class PomsNotificationManager(private val context: Context) {
    private val notificationManager = NotificationManagerCompat.from(context)
//    private var currentBuilder: NotificationCompat.Builder? = null

    fun createNotificationBuilder(
        timerState: TimerState,
    ): NotificationCompat.Builder {
        val title = when (timerState.currentState) {
            PomoState.WORK -> "Work Session ${timerState.currentWorkSession}"
            PomoState.BREAK -> "Break Time"
        }

        val minutes = timerState.currentTime / 60000
        val seconds = (timerState.currentTime % 60000) / 1000
        val content = String.format("%02d:%02d remaining", minutes, seconds)

        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_timer_24dp)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(100, (timerState.progress * 100).toInt(), false)
    }

    fun updateNotification(timerState: TimerState) {
        val notification = createNotificationBuilder(timerState)
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification.build())
//        currentBuilder?.let { builder ->
//            // Update notification content based on current state
//            builder.setContentTitle(getNotificationTitle(timerState))
//                .setContentText(getNotificationContent(timerState))
//                .setProgress(100, (timerState.progress * 100).toInt(), false)
//
//            try {
//                notificationManager.notify(NOTIFICATION_ID, builder.build())
//            } catch (e: SecurityException) {
//                // Handle notification permission not granted
//                e.printStackTrace()
//            }
//        }
    }

    private fun getNotificationTitle(timerState: TimerState): String {
        return when (timerState.currentState) {
            PomoState.WORK -> "Work Session ${timerState.currentWorkSession}"
            PomoState.BREAK -> "Break Time"
        }
    }

    private fun getNotificationContent(timerState: TimerState): String {
        val minutes = timerState.currentTime / 60000
        val seconds = (timerState.currentTime % 60000) / 1000
        return String.format("%02d:%02d remaining", minutes, seconds)
    }
}