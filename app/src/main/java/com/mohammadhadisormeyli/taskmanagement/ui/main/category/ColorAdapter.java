package com.mohammadhadisormeyli.taskmanagement.ui.main.category;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.utils.CommonUtils;
import com.mohammadhadisormeyli.taskmanagement.utils.ScreenUtils;

import java.util.List;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {


    private final List<Integer> colors;
    private final View.OnClickListener onClickListener;
    private int selectedColor = 1;

    public ColorAdapter(List<Integer> colors, View.OnClickListener onClickListener) {
        colors.add(0, 0);
        this.colors = colors;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ColorViewHolder(new ImageFilterView(parent.getContext()));
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Context context = holder.imageFilterView.getContext();
        int size = (int) ScreenUtils.toDp(40);
        int margin = (int) ScreenUtils.toDp(4);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(size, size);
        layoutParams.setMargins(margin, margin, margin, margin);
        holder.imageFilterView.setLayoutParams(layoutParams);
        holder.imageFilterView.setRoundPercent(1);
        holder.imageFilterView.setElevation(margin);
        holder.imageFilterView.setPadding(margin, margin, margin, margin);

        int color = colors.get(position);
        if (color == 0) {
            holder.imageFilterView.setBackground(ContextCompat.getDrawable(context, R.drawable.all_corner_bg));
            holder.imageFilterView.setImageResource(R.drawable.ic_add);
            holder.imageFilterView.setOnClickListener(onClickListener);
        } else {
            holder.imageFilterView.setImageResource(0);

            if (position == selectedColor) {
                LayerDrawable layerDrawable = (LayerDrawable) ContextCompat.getDrawable(context, R.drawable.selected_oval_bg);
                holder.imageFilterView.setBackgroundDrawable(layerDrawable);
                if (layerDrawable != null) {
                    layerDrawable.getDrawable(2).setTint(color);
                    ((GradientDrawable) layerDrawable.getDrawable(0)).setStroke(margin, CommonUtils.adjustAlpha(color, 0.5f));
                }
                holder.imageFilterView.setImageResource(R.drawable.ic_baseline_check_24);
                holder.imageFilterView.setOnClickListener(null);
            } else {
                holder.imageFilterView.setBackgroundColor(color);
                holder.imageFilterView.setOnClickListener(view -> {
                    selectedColor = position;
                    notifyDataSetChanged();
                });
            }
        }
    }

    public String getSelectedColor() {
        return String.format("#%06X", (0xFFFFFF & colors.get(selectedColor)));
    }

    @SuppressLint("NotifyDataSetChanged")
    public int addColor(int color) {
        if (!colors.contains(color)) {
            colors.add(1, color);
            selectedColor = 1;
        } else {
            selectedColor = colors.indexOf(color);
        }
        notifyDataSetChanged();
        return selectedColor;
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public class ColorViewHolder extends RecyclerView.ViewHolder {

        private final ImageFilterView imageFilterView;

        public ColorViewHolder(@NonNull ImageFilterView itemView) {
            super(itemView);
            imageFilterView = itemView;
        }
    }
}
