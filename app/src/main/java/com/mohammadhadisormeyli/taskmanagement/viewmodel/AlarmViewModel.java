package com.mohammadhadisormeyli.taskmanagement.viewmodel;

import com.mohammadhadisormeyli.taskmanagement.data.DataManager;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseViewModel;
import com.mohammadhadisormeyli.taskmanagement.utils.rx.SchedulerProvider;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AlarmViewModel extends BaseViewModel {

    @Inject
    public AlarmViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
    }
}
