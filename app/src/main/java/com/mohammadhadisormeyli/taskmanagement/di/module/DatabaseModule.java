package com.mohammadhadisormeyli.taskmanagement.di.module;

import android.content.Context;

import androidx.room.Room;

import com.mohammadhadisormeyli.taskmanagement.data.local.db.AppDataBase;
import com.mohammadhadisormeyli.taskmanagement.data.local.db.AppDbHelper;
import com.mohammadhadisormeyli.taskmanagement.data.local.db.DbHelper;
import com.mohammadhadisormeyli.taskmanagement.data.local.db.dao.CategoryDao;
import com.mohammadhadisormeyli.taskmanagement.data.local.db.dao.TaskDao;
import com.mohammadhadisormeyli.taskmanagement.data.local.db.dao.UserDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    AppDataBase provideAppDataBase(Context context) {
        return Room.databaseBuilder(
                context
                , AppDataBase.class
                , AppDataBase.DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }

    @Provides
    @Singleton
    DbHelper provideDbHelper(AppDbHelper appDbHelper) {
        return appDbHelper;
    }

    @Provides
    @Singleton
    TaskDao provideTaskDao(AppDataBase appDataBase) {
        return appDataBase.taskDao();
    }

    @Provides
    @Singleton
    CategoryDao provideCategoryDao(AppDataBase appDataBase) {
        return appDataBase.categoryDao();
    }

    @Provides
    @Singleton
    UserDao provideUserDao(AppDataBase appDataBase) {
        return appDataBase.userDao();
    }

}
