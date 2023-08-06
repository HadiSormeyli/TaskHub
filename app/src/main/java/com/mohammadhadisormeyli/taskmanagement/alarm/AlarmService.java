package com.mohammadhadisormeyli.taskmanagement.alarm;

import static com.mohammadhadisormeyli.taskmanagement.TaskManagementApp.CHANNEL_ID;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.data.DataManager;
import com.mohammadhadisormeyli.taskmanagement.model.SubTask;
import com.mohammadhadisormeyli.taskmanagement.model.SubTaskRelation;
import com.mohammadhadisormeyli.taskmanagement.ui.alarm.AlarmActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

@AndroidEntryPoint
public class AlarmService extends Service {

    @Inject
    DataManager dataManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long taskId = intent.getLongExtra(getString(R.string.arg_alarm_obj), -1);
        if (taskId != -1) {
            showNotification(taskId);
        }
        return START_STICKY;
    }

    private void showNotification(long taskId) {
        dataManager.getTaskById(taskId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<SubTaskRelation>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(SubTaskRelation subTaskRelation) {
                        if (subTaskRelation != null) {
                            Notification.Builder notificationBuilder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                notificationBuilder = new Notification.Builder(AlarmService.this, CHANNEL_ID);
                            } else {
                                notificationBuilder = new Notification.Builder(AlarmService.this);
                            }

                            Intent intent = new Intent(AlarmService.this, AlarmActivity.class);
                            intent.putExtra(getString(R.string.arg_alarm_obj), taskId);

                            PendingIntent pendingIntent = PendingIntent.getActivity(AlarmService.this, (int) taskId, intent, PendingIntent.FLAG_IMMUTABLE);

                            String content = "";
                            if (!subTaskRelation.subTasks.isEmpty()) {
                                int done = 0;
                                for (SubTask subTask : subTaskRelation.subTasks) {
                                    if (subTask.isDone())
                                        done++;
                                }
                                content = "You done " + done + " / " + subTaskRelation.subTasks.size();
                            }
                            Notification notification = notificationBuilder
                                    .setContentTitle(subTaskRelation.task.getTitle())
                                    .setContentText("Task time is up. " + content)
                                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                                    .setSmallIcon(R.drawable.ic_baseline_access_time_filled_24)
                                    .setPriority(Notification.PRIORITY_HIGH)
                                    .setContentIntent(pendingIntent)
                                    .build();

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(AlarmService.this);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                if (ActivityCompat.checkSelfPermission(AlarmService.this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                    notificationManager.notify((int) taskId, notification);
                                }
                            } else {
                                notificationManager.notify((int) taskId, notification);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
