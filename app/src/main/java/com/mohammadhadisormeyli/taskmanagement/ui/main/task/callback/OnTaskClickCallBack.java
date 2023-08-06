package com.mohammadhadisormeyli.taskmanagement.ui.main.task.callback;

import com.mohammadhadisormeyli.taskmanagement.model.SubTaskRelation;

public interface OnTaskClickCallBack {
    void onTaskClick(SubTaskRelation subTaskRelation);
    void onTaskDelete(SubTaskRelation subTaskRelation);
    void onTaskEdit(SubTaskRelation subTaskRelation);
}
