package com.mohammadhadisormeyli.taskmanagement.model;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.room.Embedded;
import androidx.room.Relation;

import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.alarm.AlarmBroadcastReceiver;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;


public class SubTaskRelation implements Serializable {

    @Relation(entityColumn = "taskId", parentColumn = "id")
    public List<SubTask> subTasks;

    @Embedded
    public Task task;

    @SuppressLint({"ScheduleExactAlarm"})
    public void schedule(Context context, long taskId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        intent.putExtra(context.getString(R.string.arg_alarm_obj), taskId);
        PendingIntent alarmPendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        (int) taskId,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE
                );
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(task.getEndDate());

        alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                calendar.getTimeInMillis(),
                alarmPendingIntent
        );
    }


    public void cancelAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmBroadcastReceiver.class);
        PendingIntent alarmPendingIntent =
                PendingIntent.getBroadcast(
                        context,
                        (int) task.getId(),
                        intent,
                        PendingIntent.FLAG_MUTABLE
                );
        alarmManager.cancel(alarmPendingIntent);
    }
}
