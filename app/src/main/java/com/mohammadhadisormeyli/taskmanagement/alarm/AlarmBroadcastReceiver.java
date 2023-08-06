package com.mohammadhadisormeyli.taskmanagement.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import com.mohammadhadisormeyli.taskmanagement.R;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            String toastText = "Alarm Reboot";
            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
            startRescheduleAlarmsService(context);
        } else {
            long id;
            id = intent.getLongExtra(context.getString(R.string.arg_alarm_obj), -1);
            startAlarmService(context, id);
        }
    }

    private void startAlarmService(Context context, long taskId) {
        try {
            Intent intentService = new Intent(context, AlarmService.class);
            intentService.putExtra(context.getString(R.string.arg_alarm_obj), taskId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intentService);
            } else {
                context.startService(intentService);
            }
        } catch (Exception ignored) {
        }
    }

    private void startRescheduleAlarmsService(Context context) {
        Intent intentService = new Intent(context, RescheduleAlarmsService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }
    }
}
