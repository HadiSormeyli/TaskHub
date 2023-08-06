package com.mohammadhadisormeyli.taskmanagement.data;

import com.mohammadhadisormeyli.taskmanagement.data.local.db.DbHelper;
import com.mohammadhadisormeyli.taskmanagement.data.local.prefs.PreferencesHelper;
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
public class AppDataManager implements DataManager {

    private final DbHelper dbHelper;
    private final PreferencesHelper preferencesHelper;

    @Inject
    public AppDataManager(DbHelper dbHelper, PreferencesHelper preferencesHelper) {
        this.dbHelper = dbHelper;
        this.preferencesHelper = preferencesHelper;
    }

    @Override
    public Completable insertCategory(Category category) {
        return dbHelper.insertCategory(category);
    }

    @Override
    public Completable deleteCategory(Category category) {
        return dbHelper.deleteCategory(category);
    }

    @Override
    public Flowable<List<Category>> getAllCategories() {
        return dbHelper.getAllCategories();
    }

    @Override
    public Single<List<Category>> searchCategory(String query, int orderMode, boolean sortMode) {
        return dbHelper.searchCategory(query, orderMode, sortMode);
    }

    @Override
    public Single<Long> insertTask(Task task) {
        return dbHelper.insertTask(task);
    }

    @Override
    public Completable deleteTask(Task task) {
        return dbHelper.deleteTask(task);
    }

    @Override
    public Single<Boolean> exist(long id) {
        return dbHelper.exist(id);
    }

    @Override
    public Completable insertSubTask(SubTask subTask) {
        return dbHelper.insertSubTask(subTask);
    }

    @Override
    public Completable updateSubTask(SubTask subTask) {
        return dbHelper.updateSubTask(subTask);
    }

    @Override
    public Single<SubTaskRelation> getTaskById(long id) {
        return dbHelper.getTaskById(id);
    }

    @Override
    public Single<List<SubTaskRelation>> getAllTasks() {
        return dbHelper.getAllTasks();
    }

    @Override
    public Flowable<List<SubTaskRelation>> getTasksByDate(int year, int month) {
        return dbHelper.getTasksByDate(year, month);
    }

    @Override
    public Single<List<SubTaskRelation>> getTasksByCategory(long categoryId, int orderMode, boolean sortMode) {
        return dbHelper.getTasksByCategory(categoryId, orderMode, sortMode);
    }

    @Override
    public Single<List<SubTaskRelation>> searchTask(String query, List<Long> categoriesId, String startDate, String endDate, int order, boolean isReverse) {
        return dbHelper.searchTask(query, categoriesId, startDate, endDate, order, isReverse);
    }

    @Override
    public Completable insertUser(User user) {
        return dbHelper.insertUser(user);
    }

    @Override
    public Completable updateUser(User user) {
        return dbHelper.updateUser(user);
    }

    @Override
    public Flowable<User> getUser() {
        return dbHelper.getUser();
    }

    @Override
    public int getNightMode() {
        return preferencesHelper.getNightMode();
    }

    @Override
    public void setNightMode(int isNightMode) {
        preferencesHelper.setNightMode(isNightMode);
    }

    @Override
    public boolean getReminder() {
        return preferencesHelper.getReminder();
    }

    @Override
    public void setReminder(boolean reminder) {
        preferencesHelper.setReminder(reminder);
    }

    @Override
    public void insertUser() {
        preferencesHelper.insertUser();
    }

    @Override
    public boolean checkUser() {
        return preferencesHelper.checkUser();
    }
}
