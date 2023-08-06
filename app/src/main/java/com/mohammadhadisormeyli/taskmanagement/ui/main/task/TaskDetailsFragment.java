package com.mohammadhadisormeyli.taskmanagement.ui.main.task;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.AppBarLayout;
import com.maltaisn.icondialog.pack.IconDrawableLoader;
import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.databinding.FragmentTaskDetailsBinding;
import com.mohammadhadisormeyli.taskmanagement.model.SubTask;
import com.mohammadhadisormeyli.taskmanagement.model.Task;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseFragment;
import com.mohammadhadisormeyli.taskmanagement.ui.main.task.callback.OnCheckSubtaskCallBack;
import com.mohammadhadisormeyli.taskmanagement.utils.BindingUtils;
import com.mohammadhadisormeyli.taskmanagement.utils.CommonUtils;
import com.mohammadhadisormeyli.taskmanagement.utils.ScreenUtils;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.MainViewModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class TaskDetailsFragment extends BaseFragment<FragmentTaskDetailsBinding, MainViewModel> implements OnCheckSubtaskCallBack {

    @Inject
    protected IconDrawableLoader iconDrawableLoader;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_task_details;
    }

    @Override
    public void preformViewModel() {
        viewModel = new ViewModelProvider(activity).get(MainViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpAppbar();
        observeData();
    }

    private void observeData() {
        viewModel.getSelectedTask().observe(getViewLifecycleOwner(), subTaskRelation -> {
            binding.taskDetailsDeleteIb.setOnClickListener(v ->
                    viewModel.dataManager.deleteTask(subTaskRelation.task)
                            .subscribeOn(viewModel.getSchedulerProvider().io())
                            .observeOn(viewModel.getSchedulerProvider().ui())
                            .subscribe(
                                    () -> activity.onBackPressed(),
                                    Throwable::printStackTrace
                            )
            );

            setUpTask(subTaskRelation.task);
            setUpSubTasks(subTaskRelation.subTasks);
        });
    }

    private void setUpAppbar() {
        binding.taskDetailsAppbar.addOnOffsetChangedListener((AppBarLayout.BaseOnOffsetChangedListener) (appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()
                    && binding.relativeLayout.getVisibility() == View.VISIBLE) {
                binding.relativeLayout.setVisibility(View.GONE);
            } else if (binding.relativeLayout.getVisibility() == View.GONE) {
                binding.relativeLayout.setVisibility(View.VISIBLE);
            }
        });

        binding.taskDetailsBackIb.setOnClickListener(v -> activity.onBackPressed());
    }

    private void setUpSubTasks(List<SubTask> subTasks) {
        if (subTasks.size() > 0) {
            binding.progressbarContainer.setVisibility(View.VISIBLE);
            binding.subtaskTextView.setVisibility(View.VISIBLE);
            binding.taskSubtasksRv.setVisibility(View.VISIBLE);
            binding.circularProgressView.setMaxValue(subTasks.size());
            binding.afterDateCpv.setMaxValue(subTasks.size());

            SubTaskAdapter subTaskAdapter = new SubTaskAdapter(subTasks, false);
            subTaskAdapter.setOnCheckSubtaskCallBack(this);
            binding.taskSubtasksRv.setAdapter(subTaskAdapter);

            int onTime = 0;
            int past = 0;
            for (SubTask subTask : subTasks) {
                if (subTask.isDone() && !subTask.isPast()) {
                    onTime++;
                } else if (subTask.isPast())
                    past++;
            }
            binding.afterDateCpv.setValueAnimated(onTime + past);
            binding.circularProgressView.setValueAnimated(onTime);
            binding.taskDetailsCl.setExpandedTitleMarginBottom((int) ScreenUtils.toDp(48));
        } else {
            binding.progressbarContainer.setVisibility(View.GONE);
            binding.subtaskTextView.setVisibility(View.GONE);
            binding.taskSubtasksRv.setVisibility(View.GONE);
        }
    }

    @SuppressLint("SetTextI18n")
    private void setUpTask(Task task) {
        binding.taskDetailsCl.setTitle(task.getTitle());
        BindingUtils.setTimes(
                binding.taskFinishDateTv,
                task.getStartDate(),
                task.getEndDate()
        );

        if (task.getCategory() != null) {
            iconDrawableLoader.loadDrawable(task.getCategory().getIcon());
        }

        if (TextUtils.isEmpty(task.getDescription())) {
            binding.descriptionTextView.setVisibility(View.GONE);
            binding.expandTextView.setVisibility(View.GONE);
        } else
            binding.expandTextView.setText(task.getDescription());

        int color = Color.parseColor((task.getCategory() != null) ? task.getCategory().getColor() : "#3d5afe");
        binding.taskDetailsAppbar.setBackground(CommonUtils.getBottomOvalDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM
                , ContextCompat.getColor(activity, R.color.indigo_a_400)
                , ContextCompat.getColor(activity, R.color.indigo_a_700)
                , color));


        binding.stillSubtasksTv.getCompoundDrawablesRelative()[0].setTint(CommonUtils.adjustAlpha(color, 0.2f));
        binding.pastTimeSubtasksTv.getCompoundDrawablesRelative()[0].setTint(CommonUtils.adjustAlpha(color, 0.55f));
        binding.onTimeSubtasksTv.getCompoundDrawablesRelative()[0].setTint(color);

        binding.circularProgressView.setBarColor(color);
        binding.circularProgressView.setRimColor(CommonUtils.adjustAlpha(color, 0.2f));

        binding.afterDateCpv.setBarColor(CommonUtils.adjustAlpha(color, 0.55f));
        binding.afterDateCpv.setRimColor(CommonUtils.adjustAlpha(color, 0.2f));

        if (task.getAttachments().size() > 0) {
            FileAttachmentsAdapter fileAttachmentsAdapter = new FileAttachmentsAdapter(task.getAttachments(), null);
            binding.taskAttachmentsTv.setText(task.getAttachments().size() + " files");
            binding.taskAttachmentsRv.setAdapter(fileAttachmentsAdapter);
        } else {
            binding.attachmentsTextView.setVisibility(View.GONE);
            binding.taskAttachmentsRv.setVisibility(View.GONE);
            binding.taskAttachmentsTv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCheckSubtask(List<SubTask> subTasks, int index) {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        boolean before = viewModel.getSelectedTask().getValue().task.getEndDate().after(date);
        subTasks.get(index).setPast(subTasks.get(index).isDone() && !before);

        int onTime = 0;
        int past = 0;
        for (SubTask subTask : subTasks) {
            if (subTask.isDone() && !subTask.isPast()) {
                onTime++;
            } else if (subTask.isPast())
                past++;
        }
        binding.circularProgressView.setValueAnimated(onTime);
        binding.afterDateCpv.setValueAnimated(onTime + past);

        viewModel.updateSubTasks(subTasks.get(index));
    }
}