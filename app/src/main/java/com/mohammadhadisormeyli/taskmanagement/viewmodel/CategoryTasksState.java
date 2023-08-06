package com.mohammadhadisormeyli.taskmanagement.viewmodel;

import com.mohammadhadisormeyli.taskmanagement.model.SubTaskRelation;

import java.util.ArrayList;
import java.util.List;

public class CategoryTasksState {


    public boolean isLoading = true;
    public Sort sort = new Sort();
    public List<SubTaskRelation> tasks = new ArrayList<>();

    public CategoryTasksState() {
    }

    public CategoryTasksState(boolean isLoading) {
        this.isLoading = isLoading;
    }

    public CategoryTasksState(boolean isLoading, Sort sort, List<SubTaskRelation> tasks) {
        this.isLoading = isLoading;
        this.sort = sort;
        this.tasks = tasks;
    }

    public CategoryTasksState(Sort sort) {
        this.sort = sort;
    }

    public CategoryTasksState(boolean isLoading, Sort sort) {
        this.isLoading = isLoading;
        this.sort = sort;
    }
}
