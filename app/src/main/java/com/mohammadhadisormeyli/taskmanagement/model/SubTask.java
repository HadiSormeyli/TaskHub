package com.mohammadhadisormeyli.taskmanagement.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(foreignKeys = {
        @ForeignKey(entity = Task.class,
                parentColumns = "id",
                childColumns = "taskId",
                onDelete = ForeignKey.CASCADE)})
public class SubTask implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(index = true)
    private long taskId;
    private String title;
    private boolean isDone;
    private boolean isPast;

    public SubTask(long taskId, String title, boolean isDone, boolean isPast) {
        this.taskId = taskId;
        this.title = title;
        this.isDone = isDone;
        this.isPast = isPast;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public boolean isPast() {
        return isPast;
    }

    public void setPast(boolean past) {
        isPast = past;
    }
}
