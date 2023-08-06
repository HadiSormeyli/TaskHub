package com.mohammadhadisormeyli.taskmanagement.ui.main.task.callback;

import com.mohammadhadisormeyli.taskmanagement.model.SubTask;

import java.util.List;

public interface OnCheckSubtaskCallBack {
    void onCheckSubtask(List<SubTask> subTasks, int index);
}
