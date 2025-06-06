package com.dac.lab05_alarmexercise

data class Alarm(
    val hour: Int,
    val minute: Int,
    val days: List<Int> // e.g. Calendar.MONDAY = 2, Calendar.SUNDAY = 1
)
