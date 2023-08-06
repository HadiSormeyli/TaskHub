package com.mohammadhadisormeyli.taskmanagement.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.maltaisn.icondialog.data.Icon;

import java.io.Serializable;

@Entity(indices = {@Index(value = "title", unique = true)})
public class Category implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String title;
    private String color;
    private Icon icon;

    @Ignore
    public Category() {
    }

    public Category(String title, String color, Icon icon) {
        this.title = title;
        this.color = color;
        this.icon = icon;
    }

    public Icon getIcon() {
        return icon;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
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
}
