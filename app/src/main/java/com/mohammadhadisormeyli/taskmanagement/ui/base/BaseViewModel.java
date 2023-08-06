package com.mohammadhadisormeyli.taskmanagement.ui.base;

import androidx.lifecycle.ViewModel;

import com.mohammadhadisormeyli.taskmanagement.data.DataManager;
import com.mohammadhadisormeyli.taskmanagement.utils.rx.SchedulerProvider;

public abstract class BaseViewModel extends ViewModel {

    public final DataManager dataManager;
    protected final SchedulerProvider schedulerProvider;

    public BaseViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        this.dataManager = dataManager;
        this.schedulerProvider = schedulerProvider;
    }

    public SchedulerProvider getSchedulerProvider() {
        return schedulerProvider;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}
