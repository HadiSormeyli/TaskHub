package com.mohammadhadisormeyli.taskmanagement.ui.main.task;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.shape.CornerFamily;
import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.databinding.AddSubtaskItemBinding;
import com.mohammadhadisormeyli.taskmanagement.databinding.SubtaskItemBinding;
import com.mohammadhadisormeyli.taskmanagement.model.SubTask;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseViewHolder;
import com.mohammadhadisormeyli.taskmanagement.ui.main.task.callback.OnCheckSubtaskCallBack;
import com.mohammadhadisormeyli.taskmanagement.utils.CommonUtils;
import com.mohammadhadisormeyli.taskmanagement.utils.ScreenUtils;

import java.util.Collections;
import java.util.List;

public class SubTaskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private final boolean isAddingMode;
    private final List<SubTask> items;
    private OnCheckSubtaskCallBack onCheckSubtaskCallBack;
    private LayoutInflater layoutInflater;

    public SubTaskAdapter(List<SubTask> items, boolean isAddingMode) {
        items.removeAll(Collections.singleton(null));
        this.items = items;
        this.isAddingMode = isAddingMode;

        if (this.isAddingMode)
            this.items.add(this.items.size(), null);
    }

    public void setOnCheckSubtaskCallBack(OnCheckSubtaskCallBack onCheckSubtaskCallBack) {
        this.onCheckSubtaskCallBack = onCheckSubtaskCallBack;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null)
            layoutInflater = LayoutInflater.from(parent.getContext());

        if (isAddingMode) {
            final AddSubtaskItemBinding addSubtaskItemBinding = AddSubtaskItemBinding.inflate(layoutInflater, parent, false);
            return new AddSubTaskViewHolder(addSubtaskItemBinding);
        }
        final SubtaskItemBinding subtaskItemBinding = SubtaskItemBinding.inflate(layoutInflater, parent, false);
        return new SubTaskViewHolder(subtaskItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (isAddingMode) {
            ((AddSubTaskViewHolder) holder).onBind(items.get(position));
        } else {
            ((SubTaskViewHolder) holder).onBind(items.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private class AddSubTaskViewHolder extends BaseViewHolder<SubTask, AddSubtaskItemBinding> {


        private final TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                items.get(getAbsoluteAdapterPosition()).setTitle(editable.toString());
            }
        };

        public AddSubTaskViewHolder(AddSubtaskItemBinding itemBinding) {
            super(itemBinding);
        }

        @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
        @Override
        public void onBind(SubTask subTask) {
            float radius = ScreenUtils.toDp(8);
            if (getAbsoluteAdapterPosition() == items.size() - 1) {
                if (items.size() > 1)
                    itemBinding.addSubtaskCv.setShapeAppearanceModel(
                            itemBinding.addSubtaskCv.getShapeAppearanceModel()
                                    .toBuilder()
                                    .setTopLeftCorner(CornerFamily.ROUNDED, radius / 4)
                                    .setTopRightCorner(CornerFamily.ROUNDED, radius / 4)
                                    .setBottomRightCorner(CornerFamily.ROUNDED, radius)
                                    .setBottomLeftCornerSize(radius)
                                    .build());
                else
                    itemBinding.addSubtaskCv.setShapeAppearanceModel(
                            itemBinding.addSubtaskCv.getShapeAppearanceModel()
                                    .toBuilder()
                                    .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                                    .setTopRightCorner(CornerFamily.ROUNDED, radius)
                                    .setBottomRightCorner(CornerFamily.ROUNDED, radius)
                                    .setBottomLeftCornerSize(radius)
                                    .build());

                itemBinding.subtaskTitleEt.removeTextChangedListener(textWatcher);

                itemBinding.subtaskTitleEt.setVisibility(View.GONE);

                itemBinding.removeSubtaskIv.setImageResource(R.drawable.ic_add);
                itemBinding.removeSubtaskIv.setClickable(false);

                itemBinding.getRoot().setOnClickListener(view -> {
                    items.add(items.size() - 1, new SubTask(0, "", false, false));
                    notifyDataSetChanged();
                });
            } else {
                if (getAbsoluteAdapterPosition() == 0) {
                    itemBinding.addSubtaskCv.setShapeAppearanceModel(
                            itemBinding.addSubtaskCv.getShapeAppearanceModel()
                                    .toBuilder()
                                    .setTopLeftCorner(CornerFamily.ROUNDED, radius)
                                    .setTopRightCorner(CornerFamily.ROUNDED, radius)
                                    .setBottomRightCorner(CornerFamily.ROUNDED, radius / 4)
                                    .setBottomLeftCornerSize(radius / 4)
                                    .build());
                } else {
                    itemBinding.addSubtaskCv.setShapeAppearanceModel(
                            itemBinding.addSubtaskCv.getShapeAppearanceModel()
                                    .toBuilder()
                                    .setTopLeftCorner(CornerFamily.ROUNDED, radius / 4)
                                    .setTopRightCorner(CornerFamily.ROUNDED, radius / 4)
                                    .setBottomRightCorner(CornerFamily.ROUNDED, radius / 4)
                                    .setBottomLeftCornerSize(radius / 4)
                                    .build());
                }
                if (subTask != null) {
                    itemBinding.subtaskTitleEt.setText(subTask.getTitle());
                    itemBinding.subtaskTitleEt.setVisibility(View.VISIBLE);

                    itemBinding.subtaskTitleEt.addTextChangedListener(textWatcher);

                    itemBinding.removeSubtaskIv.setImageResource(R.drawable.ic_close_round);
                    itemBinding.removeSubtaskIv.setClickable(true);
                    itemBinding.removeSubtaskIv.setOnClickListener(view -> {
                        items.remove(getAbsoluteAdapterPosition());
                        notifyDataSetChanged();
                    });


                    itemBinding.getRoot().setOnClickListener(null);
                }
            }

            itemBinding.executePendingBindings();
        }
    }

    private class SubTaskViewHolder extends BaseViewHolder<SubTask, SubtaskItemBinding> {

        public SubTaskViewHolder(SubtaskItemBinding itemBinding) {
            super(itemBinding);
        }

        @Override
        public void onBind(SubTask subtask) {
            itemBinding.subtaskCheckbox.setChecked(subtask.isDone());

            itemBinding.subtaskCheckbox.setButtonTintList
                    (CommonUtils.getCheckBoxColorStateList(ContextCompat.getColor(itemView.getContext(), R.color.grey_500)
                            , ContextCompat.getColor(itemView.getContext(), R.color.light_green_a_600)));
            itemBinding.subtaskCheckbox.setText(subtask.getTitle());
            itemBinding.subtaskCheckbox.setEnabled((onCheckSubtaskCallBack != null));
            if (subtask.isDone()) startStrikeThroughAnimation();

            if (onCheckSubtaskCallBack != null)
                itemBinding.subtaskCheckbox.setOnCheckedChangeListener((compoundButton, b) -> {
                    subtask.setDone(b);
                    subtask.setPast(false);
                    if (b) startStrikeThroughAnimation();
                    else reverseStrikeThroughAnimation();
                    onCheckSubtaskCallBack.onCheckSubtask(items, getAbsoluteAdapterPosition());
                });

            if (getAbsoluteAdapterPosition() == items.size() - 1) {
                itemBinding.subTaskDividerView.setVisibility(View.GONE);
            }

//            float radius = ScreenUtils.toDp(8);
//            if (getAbsoluteAdapterPosition() == items.size() - 1) {
//                if (items.size() > 1) {
//                    itemBinding.subtaskCardView.setShapeAppearanceModel(
//                            itemBinding.subtaskCardView.getShapeAppearanceModel()
//                                    .toBuilder()
//                                    .setTopLeftCorner(CornerFamily.ROUNDED, radius / 4)
//                                    .setTopRightCorner(CornerFamily.ROUNDED, radius / 4)
//                                    .setBottomRightCorner(CornerFamily.ROUNDED, radius)
//                                    .setBottomLeftCornerSize(radius)
//                                    .build());
//                } else {
//                    itemBinding.subtaskCardView.setShapeAppearanceModel(
//                            itemBinding.subtaskCardView.getShapeAppearanceModel()
//                                    .toBuilder()
//                                    .setTopLeftCorner(CornerFamily.ROUNDED, radius)
//                                    .setTopRightCorner(CornerFamily.ROUNDED, radius)
//                                    .setBottomRightCorner(CornerFamily.ROUNDED, radius)
//                                    .setBottomLeftCornerSize(radius)
//                                    .build());
//                }
//            } else {
//                if (getAbsoluteAdapterPosition() == 0) {
//                    itemBinding.subtaskCardView.setShapeAppearanceModel(
//                            itemBinding.subtaskCardView.getShapeAppearanceModel()
//                                    .toBuilder()
//                                    .setTopLeftCorner(CornerFamily.ROUNDED, radius)
//                                    .setTopRightCorner(CornerFamily.ROUNDED, radius)
//                                    .setBottomRightCorner(CornerFamily.ROUNDED, radius / 4)
//                                    .setBottomLeftCornerSize(radius / 4)
//                                    .build());
//                } else {
//                    itemBinding.subtaskCardView.setShapeAppearanceModel(
//                            itemBinding.subtaskCardView.getShapeAppearanceModel()
//                                    .toBuilder()
//                                    .setTopLeftCorner(CornerFamily.ROUNDED, radius / 4)
//                                    .setTopRightCorner(CornerFamily.ROUNDED, radius / 4)
//                                    .setBottomRightCorner(CornerFamily.ROUNDED, radius / 4)
//                                    .setBottomLeftCornerSize(radius / 4)
//                                    .build());
//                }
//            }
            itemBinding.executePendingBindings();
        }

        private void startStrikeThroughAnimation() {
            SpannableString span = new SpannableString(itemBinding.subtaskCheckbox.getText());
            StrikethroughSpan strikeSpan = new StrikethroughSpan();
            ValueAnimator animator = ValueAnimator.ofInt(itemBinding.subtaskCheckbox.getText().length());
            animator.addUpdateListener(animation -> {
                span.setSpan(strikeSpan, 0, (int) animator.getAnimatedValue(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                itemBinding.subtaskCheckbox.setText(span);
            });
            animator.start();
        }

        private void reverseStrikeThroughAnimation() {
            SpannableString span = new SpannableString(itemBinding.subtaskCheckbox.getText() + "");
            StrikethroughSpan strikeSpan = new StrikethroughSpan();
            ValueAnimator animator = ValueAnimator.ofInt(itemBinding.subtaskCheckbox.getText().length(), 0);
            animator.addUpdateListener(animation -> {
                span.setSpan(strikeSpan, 0, (int) animator.getAnimatedValue(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                itemBinding.subtaskCheckbox.setText(span);
            });
            animator.start();
        }
    }
}
