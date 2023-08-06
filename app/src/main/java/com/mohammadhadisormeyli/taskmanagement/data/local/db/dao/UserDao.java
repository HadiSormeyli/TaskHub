package com.mohammadhadisormeyli.taskmanagement.data.local.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mohammadhadisormeyli.taskmanagement.model.User;

import io.reactivex.Completable;
import io.reactivex.Flowable;

@Dao
public interface UserDao {

    @Insert
    Completable insert(User user);

    @Update
    Completable update(User user);

    @Query("select * from user limit 1")
    Flowable<User> getUser();
}
