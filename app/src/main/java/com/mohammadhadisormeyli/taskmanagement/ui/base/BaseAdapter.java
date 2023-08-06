package com.mohammadhadisormeyli.taskmanagement.ui.base;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public abstract class BaseAdapter<T> extends ListAdapter<T, BaseViewHolder> {
    protected LayoutInflater layoutInflater;

    protected BaseAdapter(DiffUtil.ItemCallback diffCallback) {
        super(diffCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.onBind(getItem(position));
    }
}