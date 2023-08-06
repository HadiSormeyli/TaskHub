package com.mohammadhadisormeyli.taskmanagement.ui.alarm;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.appbar.AppBarLayout;
import com.maltaisn.icondialog.pack.IconDrawableLoader;
import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.databinding.ActivityAlarmBinding;
import com.mohammadhadisormeyli.taskmanagement.model.SubTask;
import com.mohammadhadisormeyli.taskmanagement.model.SubTaskRelation;
import com.mohammadhadisormeyli.taskmanagement.model.Task;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseActivity;
import com.mohammadhadisormeyli.taskmanagement.ui.main.task.FileAttachmentsAdapter;
import com.mohammadhadisormeyli.taskmanagement.ui.main.task.SubTaskAdapter;
import com.mohammadhadisormeyli.taskmanagement.utils.BindingUtils;
import com.mohammadhadisormeyli.taskmanagement.utils.CommonUtils;
import com.mohammadhadisormeyli.taskmanagement.utils.ScreenUtils;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.AlarmViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

@AndroidEntryPoint
public class AlarmActivity extends BaseActivity<ActivityAlarmBinding, AlarmViewModel> {

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_alarm;
    }

    @Override
    public void preformViewModel() {
        viewModel = new ViewModelProvider(this).get(AlarmViewModel.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            long id = getIntent().getLongExtra(getString(R.string.arg_alarm_obj), -1);
            viewModel.dataManager.getTaskById(id)
                    .subscribeOn(viewModel.getSchedulerProvider().io())
                    .observeOn(viewModel.getSchedulerProvider().ui())
                    .subscribe(new SingleObserver<SubTaskRelation>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(SubTaskRelation subTaskRelation) {
                            if (subTaskRelation != null) {
                                setUpAppbar();
                                observeData(subTaskRelation);
                            } else {
                                finish();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            finish();
                        }
                    });

        }
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
    }


    private void observeData(SubTaskRelation subTaskRelation) {
        setUpTask(subTaskRelation.task);
        setUpSubTasks(subTaskRelation.subTasks);
    }

    private void setUpSubTasks(List<SubTask> subTasks) {
        if (subTasks.size() > 0) {
            binding.progressbarContainer.setVisibility(View.VISIBLE);
            binding.subtaskTextView.setVisibility(View.VISIBLE);
            binding.taskSubtasksRv.setVisibility(View.VISIBLE);
            binding.circularProgressView.setMaxValue(subTasks.size());
            binding.afterDateCpv.setMaxValue(subTasks.size());

            SubTaskAdapter subTaskAdapter = new SubTaskAdapter(subTasks, false);
            binding.taskSubtasksRv.setAdapter(subTaskAdapter);

            int onTime = 0;
            int past = 0;
            for (SubTask subTask : subTasks) {
                if (subTask.isDone()) {
                    onTime++;
                    if (subTask.isPast())
                        past++;
                }
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
            IconDrawableLoader iconDrawableLoader = new IconDrawableLoader(this);
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
                , ContextCompat.getColor(this, R.color.indigo_a_400)
                , ContextCompat.getColor(this, R.color.indigo_a_700)
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

}