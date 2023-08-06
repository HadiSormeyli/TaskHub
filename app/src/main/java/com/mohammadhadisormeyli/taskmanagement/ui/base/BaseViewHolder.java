package com.mohammadhadisormeyli.taskmanagement.ui.base;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseViewHolder<T, V extends ViewDataBinding> extends RecyclerView.ViewHolder {

    public V itemBinding;

    public BaseViewHolder(V itemBinding) {
        super(itemBinding.getRoot());
        this.itemBinding = itemBinding;
    }

    public abstract void onBind(T t);
}