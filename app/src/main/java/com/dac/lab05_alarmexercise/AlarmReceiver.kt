package com.dac.lab05_alarmexercise

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.media.Ringtone
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Toast.makeText(context, "Alarm ringing!", Toast.LENGTH_LONG).show()

        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(2000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(2000)
        }

        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val ringtone: Ringtone = RingtoneManager.getRingtone(context, alarmUri)
        ringtone.play()

        val hour = intent?.getIntExtra("hour", -1) ?: return
        val minute = intent.getIntExtra("minute", -1)
        val dayOfWeek = intent.getIntExtra("dayOfWeek", -1)

        if (hour >= 0 && minute >= 0 && dayOfWeek > 0) {
            val alarm = Alarm(hour, minute, listOf(dayOfWeek))
            val scheduler = AlarmScheduler(context)
            scheduler.scheduleAlarm(alarm)
        }

        // Optional: Show a notification
        NotificationHelper(context).showNotification("Alarm", "It's time!")
    }
}
