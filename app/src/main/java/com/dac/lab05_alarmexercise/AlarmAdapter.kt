package com.dac.lab05_alarmexercise

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class AlarmAdapter(
    private val alarms: MutableList<Alarm>,
    private val onDeleteClick: (position: Int) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    class AlarmViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val timeText: TextView = view.findViewById(R.id.timeText)
        val daysText: TextView = view.findViewById(R.id.daysText)
        val deleteBtn: View = view.findViewById(R.id.deleteBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.timeText.text = String.format("%02d:%02d", alarm.hour, alarm.minute)

        val dayNames = alarm.days.map { dayOfWeekToString(it) }
        holder.daysText.text = dayNames.joinToString(", ")

        holder.deleteBtn.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos != RecyclerView.NO_POSITION) {
                onDeleteClick(pos)
            }
        }
    }

    override fun getItemCount(): Int = alarms.size

    private fun dayOfWeekToString(day: Int): String = when (day) {
        Calendar.MONDAY -> "Mon"
        Calendar.TUESDAY -> "Tue"
        Calendar.WEDNESDAY -> "Wed"
        Calendar.THURSDAY -> "Thu"
        Calendar.FRIDAY -> "Fri"
        Calendar.SATURDAY -> "Sat"
        Calendar.SUNDAY -> "Sun"
        else -> "?"
    }

    fun removeAt(position: Int) {
        if (position in alarms.indices) {
            alarms.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, alarms.size - position)
        }
    }

    fun scheduleAlarm(context: Context, alarm: Alarm) {
        val scheduler = AlarmScheduler(context)
        scheduler.scheduleAlarm(alarm)
    }
}
