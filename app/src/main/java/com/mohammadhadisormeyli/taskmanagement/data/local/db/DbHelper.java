package com.mohammadhadisormeyli.taskmanagement.data.local.db;

import com.mohammadhadisormeyli.taskmanagement.model.Category;
import com.mohammadhadisormeyli.taskmanagement.model.SubTask;
import com.mohammadhadisormeyli.taskmanagement.model.SubTaskRelation;
import com.mohammadhadisormeyli.taskmanagement.model.Task;
import com.mohammadhadisormeyli.taskmanagement.model.User;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface DbHelper {

    Completable insertCategory(Category category);

    Completable deleteCategory(Category category);

    Flowable<List<Category>> getAllCategories();

    Single<List<Category>> searchCategory(String query, int orderMode, boolean sortMode);

    Single<Long> insertTask(Task task);

    Completable deleteTask(Task task);

    Single<Boolean> exist(long id);

    Completable insertSubTask(SubTask subTask);

    Completable updateSubTask(SubTask subTask);

    Single<SubTaskRelation> getTaskById(long id);

    Single<List<SubTaskRelation>> getAllTasks();

    Flowable<List<SubTaskRelation>> getTasksByDate(int year, int month);

    Single<List<SubTaskRelation>> getTasksByCategory(long categoryId, int orderMode, boolean sortMode);

    Single<List<SubTaskRelation>> searchTask(String query,
                                             List<Long> categoriesId,
                                             String startDate,
                                             String endDate,
                                             int order,
                                             boolean isReverse);

    Completable insertUser(User user);

    Completable updateUser(User user);

    Flowable<User> getUser();
}
