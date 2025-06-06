package com.dac.lab05_alarmexercise

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var timePicker: TimePicker
    private lateinit var setAlarmBtn: Button
    private lateinit var recyclerView: RecyclerView

    private val gson = Gson()
    private val prefs by lazy { getSharedPreferences("alarms", Context.MODE_PRIVATE) }
    private var alarmList = mutableListOf<Alarm>()

    private lateinit var monCheck: CheckBox
    private lateinit var tueCheck: CheckBox
    private lateinit var wedCheck: CheckBox
    private lateinit var thuCheck: CheckBox
    private lateinit var friCheck: CheckBox
    private lateinit var satCheck: CheckBox
    private lateinit var sunCheck: CheckBox

    private lateinit var adapter: AlarmAdapter
    private lateinit var alarmScheduler: AlarmScheduler

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "Notification permission denied!", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timePicker = findViewById(R.id.timePicker)
        monCheck = findViewById(R.id.monCheck)
        tueCheck = findViewById(R.id.tueCheck)
        wedCheck = findViewById(R.id.wedCheck)
        thuCheck = findViewById(R.id.thuCheck)
        friCheck = findViewById(R.id.friCheck)
        satCheck = findViewById(R.id.satCheck)
        sunCheck = findViewById(R.id.sunCheck)

        setAlarmBtn = findViewById(R.id.setAlarmBtn)
        recyclerView = findViewById(R.id.alarmRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        alarmScheduler = AlarmScheduler(this)

        loadAlarms()
        setupRecyclerView()

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MINUTE, 1)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
            timePicker.minute = calendar.get(Calendar.MINUTE)
        } else {
            timePicker.currentHour = calendar.get(Calendar.HOUR_OF_DAY)
            timePicker.currentMinute = calendar.get(Calendar.MINUTE)
        }

        // Yêu cầu cấp quyền thông báo (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setAlarmBtn.setOnClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute

            val daysSelected = mutableListOf<Int>()
            if (monCheck.isChecked) daysSelected.add(Calendar.MONDAY)
            if (tueCheck.isChecked) daysSelected.add(Calendar.TUESDAY)
            if (wedCheck.isChecked) daysSelected.add(Calendar.WEDNESDAY)
            if (thuCheck.isChecked) daysSelected.add(Calendar.THURSDAY)
            if (friCheck.isChecked) daysSelected.add(Calendar.FRIDAY)
            if (satCheck.isChecked) daysSelected.add(Calendar.SATURDAY)
            if (sunCheck.isChecked) daysSelected.add(Calendar.SUNDAY)

            if (daysSelected.isEmpty()) {
                Toast.makeText(this, "Select at least one day", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val alarm = Alarm(hour, minute, daysSelected)
            alarmList.add(alarm)
            saveAlarms()
            adapter.notifyItemInserted(alarmList.size - 1)

            alarmScheduler.scheduleAlarm(alarm)

            Toast.makeText(this, "Alarm set", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadAlarms() {
        val json = prefs.getString("alarms", "[]")
        val type = object : TypeToken<List<Alarm>>() {}.type
        alarmList = gson.fromJson(json, type)
    }

    private fun saveAlarms() {
        val json = gson.toJson(alarmList)
        prefs.edit().putString("alarms", json).apply()
    }

    private fun setupRecyclerView() {
        adapter = AlarmAdapter(alarmList) { position ->
            alarmScheduler.cancelAlarm(alarmList[position])
            alarmList.removeAt(position)
            adapter.notifyItemRemoved(position)
            saveAlarms()
        }
        recyclerView.adapter = adapter
    }
}
