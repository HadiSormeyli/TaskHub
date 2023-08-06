package com.mohammadhadisormeyli.taskmanagement.data.local.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.mohammadhadisormeyli.taskmanagement.model.Category;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface CategoryDao {


    @Insert
    Completable insert(Category category);

    @Delete
    Completable delete(Category category);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    Completable update(Category category);


    @Query("SELECT * FROM CATEGORY ORDER BY id desc")
    Flowable<List<Category>> getAllCategories();

    @Query("SELECT * FROM CATEGORY where title like '%' || :query || '%'" +
            " order by " +
            " case when :orderMode = 1 then id end desc" +
            ",case when :orderMode = 0 and :sortMode = 0 then title end asc" +
            ",case when :orderMode = 0 and :sortMode = 1 then title end desc")
    Single<List<Category>> searchCategory(String query, int orderMode, boolean sortMode);
}
