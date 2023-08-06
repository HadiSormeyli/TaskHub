package com.mohammadhadisormeyli.taskmanagement.viewmodel;

import static com.mohammadhadisormeyli.taskmanagement.viewmodel.Status.ERROR;
import static com.mohammadhadisormeyli.taskmanagement.viewmodel.Status.LOADING;
import static com.mohammadhadisormeyli.taskmanagement.viewmodel.Status.SUCCESS;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Resource<T> {

    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final Throwable error;

    private Resource(Status status, @Nullable T data, @Nullable Throwable error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(LOADING, null, null);
    }

    public static <T> Resource<T> success(@NonNull T data) {
        return new Resource<>(SUCCESS, data, null);
    }

    public static <T> Resource<T> error(@NonNull Throwable error) {
        return new Resource<>(ERROR, null, error);
    }
}
