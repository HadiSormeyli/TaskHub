package com.mohammadhadisormeyli.taskmanagement.ui.main.category;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DiffUtil;

import com.google.android.material.card.MaterialCardView;
import com.mohammadhadisormeyli.taskmanagement.databinding.CategoryItemBinding;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseAdapter;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseViewHolder;
import com.mohammadhadisormeyli.taskmanagement.model.Category;
import com.mohammadhadisormeyli.taskmanagement.ui.main.category.callback.OnCategoryClick;
import com.mohammadhadisormeyli.taskmanagement.utils.CommonUtils;
import com.mohammadhadisormeyli.taskmanagement.utils.ScreenUtils;

public class CategoryAdapter extends BaseAdapter<Category> {

    private static final DiffUtil.ItemCallback<Category> DiffCallback = new DiffUtil.ItemCallback<Category>() {
        @Override
        public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem == newItem;
        }
    };
    private final OnCategoryClick categoryCallback;

    public CategoryAdapter(OnCategoryClick categoryCallback) {
        super(DiffCallback);
        this.categoryCallback = categoryCallback;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CategoryItemBinding itemBinding = CategoryItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(itemBinding);
    }

    protected class CategoryViewHolder extends BaseViewHolder<Category, CategoryItemBinding> {

        public CategoryViewHolder(CategoryItemBinding itemBinding) {
            super(itemBinding);
        }

        @Override
        public void onBind(Category category) {
            int remind = getAbsoluteAdapterPosition() % 2;
            if (remind == 0) {
                itemBinding.categoryItemContainer.getLayoutParams().height = (int) ScreenUtils.toDp(208);
            }

            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                itemBinding.categoryIconIfv.setElevation(ScreenUtils.toDp(4));
                int color = ((MaterialCardView) itemBinding.getRoot()).getCardBackgroundColor().getDefaultColor();
                ((MaterialCardView) itemBinding.getRoot()).setCardBackgroundColor(CommonUtils.adjustAlpha(color, 0.8f));
            }

            itemBinding.getRoot().setOnClickListener(view -> categoryCallback.onCategoryClick(getAbsoluteAdapterPosition(), category));

            itemBinding.setCategory(category);
            itemBinding.executePendingBindings();
        }
    }
}
