package com.mohammadhadisormeyli.taskmanagement.viewmodel;

import java.util.ArrayList;
import java.util.List;

public class SearchFilterState {

    private boolean containsCategories;
    private boolean containsTasks;
    private List<Long> categoriesId;
    private String startDate;
    private String endDate;
    private Sort sort;
    private String query;

    public SearchFilterState() {
        categoriesId = new ArrayList<>();
        reset();
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void reset() {
        containsCategories = true;
        containsTasks = true;
        categoriesId.clear();
        startDate = null;
        endDate = null;
        query = null;
        sort = new Sort();
    }

    public boolean isContainsCategories() {
        return containsCategories;
    }

    public void setContainsCategories(boolean containsCategories) {
        this.containsCategories = containsCategories;
    }

    public boolean isContainsTasks() {
        return containsTasks;
    }

    public void setContainsTasks(boolean containsTasks) {
        this.containsTasks = containsTasks;
    }

    public List<Long> getCategoriesId() {
        return categoriesId;
    }

    public void setCategoriesId(List<Long> categoriesId) {
        this.categoriesId = categoriesId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Sort getSort() {
        return sort;
    }

    public void setSort(Sort sort) {
        this.sort = sort;
    }
}
