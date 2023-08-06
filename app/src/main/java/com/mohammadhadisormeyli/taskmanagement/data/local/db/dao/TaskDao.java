package com.mohammadhadisormeyli.taskmanagement.data.local.db.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.mohammadhadisormeyli.taskmanagement.model.SubTask;
import com.mohammadhadisormeyli.taskmanagement.model.SubTaskRelation;
import com.mohammadhadisormeyli.taskmanagement.model.Task;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface TaskDao {

    @Insert
    Single<Long> insert(Task task);

    @Delete
    Completable delete(Task task);

    @Update
    Completable update(Task task);

    @Query("SELECT EXISTS (SELECT 1 FROM task WHERE id = :id)")
    Single<Boolean> exist(long id);

    @Insert
    Completable insert(SubTask subTask);

    @Delete
    Completable delete(SubTask subTask);

    @Update
    Completable update(SubTask subTask);

    @Transaction
    @Query("SELECT * FROM task where id = :id")
    Single<SubTaskRelation> getTaskById(long id);

    @Transaction
    @Query("SELECT * FROM task")
    Single<List<SubTaskRelation>> getAllTasks();

    @Transaction
    @Query("SELECT * FROM TASK WHERE CAST(strftime('%Y',startDate) AS INTEGER) = :year AND CAST(strftime('%m',startDate) AS INTEGER) = :month ORDER BY startDate")
    Flowable<List<SubTaskRelation>> getTaskByDate(int year, int month);

    @Transaction
    @Query("SELECT * FROM Task WHERE categoryId=:categoryId order by " +
            "case when :orderMode = 0 and :sortMode = 0 then title end asc" +
            ", case when :orderMode = 1 and :sortMode = 0 then startdate end asc" +
            ", case when :orderMode = 0 and :sortMode = 1 then title end desc" +
            ", case when :orderMode = 1 and :sortMode = 1 then startdate end desc")
    Single<List<SubTaskRelation>> getTaskByCategory(long categoryId, int orderMode, boolean sortMode);


    @Transaction
    @Query("SELECT * FROM Task" +
            " WHERE title like '%' || :query || '%'" +
            " and (:emptyList or categoryId in(:categoriesId))" +
            " and (:startDate is null or startDate >= :startDate)" +
            " and (:endDate is null or endDate <= :endDate) order by " +
            " case when :order = 0 and :isReverse = 0 then title end asc" +
            ",case when :order = 1 and :isReverse = 0 then startdate end asc" +
            ",case when :order = 0 and :isReverse = 1 then title end desc" +
            ",case when :order = 1 and :isReverse = 1 then startdate end desc")
    Single<List<SubTaskRelation>> searchTask(
            String query,
            List<Long> categoriesId,
            boolean emptyList,
            String startDate,
            String endDate,
            int order,
            boolean isReverse);
}
