package com.mohammadhadisormeyli.taskmanagement.data.local.db;

import com.mohammadhadisormeyli.taskmanagement.model.Category;
import com.mohammadhadisormeyli.taskmanagement.model.SubTask;
import com.mohammadhadisormeyli.taskmanagement.model.SubTaskRelation;
import com.mohammadhadisormeyli.taskmanagement.model.Task;
import com.mohammadhadisormeyli.taskmanagement.model.User;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Singleton
public class AppDbHelper implements DbHelper {

    private final AppDataBase appDataBase;

    @Inject
    public AppDbHelper(AppDataBase appDatabase) {
        this.appDataBase = appDatabase;
    }

    @Override
    public Completable insertCategory(Category category) {
        return appDataBase.categoryDao().insert(category);
    }

    @Override
    public Completable deleteCategory(Category category) {
        return appDataBase.categoryDao().delete(category);
    }

    @Override
    public Flowable<List<Category>> getAllCategories() {
        return appDataBase.categoryDao().getAllCategories();
    }

    @Override
    public Single<List<Category>> searchCategory(String query, int orderMode, boolean sortMode) {
        return appDataBase.categoryDao().searchCategory(query, orderMode, sortMode);
    }

    @Override
    public Single<Long> insertTask(Task task) {
        return appDataBase.taskDao().insert(task);
    }

    @Override
    public Completable deleteTask(Task task) {
        return appDataBase.taskDao().delete(task);
    }

    @Override
    public Single<Boolean> exist(long id) {
        return appDataBase.taskDao().exist(id);
    }

    @Override
    public Completable insertSubTask(SubTask subTask) {
        return appDataBase.taskDao().insert(subTask);
    }

    @Override
    public Completable updateSubTask(SubTask subTask) {
        return appDataBase.taskDao().update(subTask);
    }

    @Override
    public Single<SubTaskRelation> getTaskById(long id) {
        return appDataBase.taskDao().getTaskById(id);
    }

    @Override
    public Single<List<SubTaskRelation>> getAllTasks() {
        return appDataBase.taskDao().getAllTasks();
    }

    @Override
    public Flowable<List<SubTaskRelation>> getTasksByDate(int year, int month) {
        return appDataBase.taskDao().getTaskByDate(year, month);
    }

    @Override
    public Single<List<SubTaskRelation>> getTasksByCategory(long categoryId, int orderMode, boolean sortMode) {
        return appDataBase.taskDao().getTaskByCategory(categoryId, orderMode, sortMode);
    }

    @Override
    public Single<List<SubTaskRelation>> searchTask(String query, List<Long> categoriesId, String startDate, String endDate, int order, boolean isReverse) {
        return appDataBase.taskDao().searchTask(query, categoriesId, categoriesId.isEmpty(), startDate, endDate, order, isReverse);
    }

    @Override
    public Completable insertUser(User user) {
        return appDataBase.userDao().insert(user);
    }

    @Override
    public Completable updateUser(User user) {
        return appDataBase.userDao().update(user);
    }

    @Override
    public Flowable<User> getUser() {
        return appDataBase.userDao().getUser();
    }
}
