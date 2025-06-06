package com.dac.lab05_alarmexercise

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import java.util.*

class AlarmScheduler(private val context: Context) {

    fun scheduleAlarm(alarm: Alarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val now = Calendar.getInstance()

        for (dayOfWeek in alarm.days) {
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, alarm.hour)
                set(Calendar.MINUTE, alarm.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            var daysToAdd = dayOfWeek - currentDayOfWeek
            if (daysToAdd < 0 || (daysToAdd == 0 && calendar.before(now))) {
                daysToAdd += 7
            }
            calendar.add(Calendar.DAY_OF_YEAR, daysToAdd)

            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("hour", alarm.hour)
                putExtra("minute", alarm.minute)
                putExtra("dayOfWeek", dayOfWeek)
            }

            val requestCode = alarm.hour * 10000 + alarm.minute * 100 + dayOfWeek
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        calendar.timeInMillis,
                        pendingIntent
                    )
                }
            } catch (e: SecurityException) {
                Toast.makeText(context, "Exact alarm permission denied!", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

    fun cancelAlarm(alarm: Alarm) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        for (dayOfWeek in alarm.days) {
            val intent = Intent(context, AlarmReceiver::class.java)
            val requestCode = alarm.hour * 10000 + alarm.minute * 100 + dayOfWeek
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)
        }
    }
}
