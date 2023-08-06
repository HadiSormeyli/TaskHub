package com.mohammadhadisormeyli.taskmanagement.alarm;

import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;

import com.mohammadhadisormeyli.taskmanagement.data.DataManager;
import com.mohammadhadisormeyli.taskmanagement.model.SubTaskRelation;

import javax.inject.Inject;

import dagger.hilt.android.scopes.ServiceScoped;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@ServiceScoped
public class RescheduleAlarmsService extends LifecycleService {

    @Inject
    public DataManager dataManager;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);


        dataManager.getAllTasks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        tasks -> {
                            for (SubTaskRelation subTaskRelation : tasks) {
                                subTaskRelation.schedule(getApplicationContext(), subTaskRelation.task.getId());
                            }
                        }
                );

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }
}
