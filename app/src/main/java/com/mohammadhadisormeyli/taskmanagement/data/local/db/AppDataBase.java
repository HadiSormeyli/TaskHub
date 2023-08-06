package com.mohammadhadisormeyli.taskmanagement.data.local.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.mohammadhadisormeyli.taskmanagement.data.local.db.dao.CategoryDao;
import com.mohammadhadisormeyli.taskmanagement.data.local.db.dao.TaskDao;
import com.mohammadhadisormeyli.taskmanagement.data.local.db.dao.UserDao;
import com.mohammadhadisormeyli.taskmanagement.model.Category;
import com.mohammadhadisormeyli.taskmanagement.model.SubTask;
import com.mohammadhadisormeyli.taskmanagement.model.Task;
import com.mohammadhadisormeyli.taskmanagement.model.User;
import com.mohammadhadisormeyli.taskmanagement.utils.RoomConverters;

@Database(entities = {Category.class, Task.class, SubTask.class, User.class}, version = 1, exportSchema = false)
@TypeConverters(RoomConverters.class)
public abstract class AppDataBase extends RoomDatabase {

    public static final String DATABASE_NAME = "mohammadhadisormeyli.taskmanagment.db";

    public abstract TaskDao taskDao();

    public abstract CategoryDao categoryDao();

    public abstract UserDao userDao();
}
