package com.mohammadhadisormeyli.taskmanagement.ui.main.task;


import android.Manifest;
import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.google.android.material.chip.Chip;
import com.maltaisn.icondialog.data.Icon;
import com.maltaisn.icondialog.pack.IconDrawableLoader;
import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.databinding.FragmentAddTaskBinding;
import com.mohammadhadisormeyli.taskmanagement.model.Category;
import com.mohammadhadisormeyli.taskmanagement.model.SubTask;
import com.mohammadhadisormeyli.taskmanagement.model.SubTaskRelation;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseFragment;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.calendar.widget.CalenderType;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.calendar.widget.CollapsibleCalendar;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.FilePicker;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.FileType;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.filepicker.data.model.Media;
import com.mohammadhadisormeyli.taskmanagement.ui.main.task.callback.onAddFileCallBack;
import com.mohammadhadisormeyli.taskmanagement.utils.CommonUtils;
import com.mohammadhadisormeyli.taskmanagement.utils.DateUtils;
import com.mohammadhadisormeyli.taskmanagement.utils.ScreenUtils;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.MainViewModel;
import com.permissionx.guolindev.PermissionX;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

@AndroidEntryPoint
public class AddTaskFragment extends BaseFragment<FragmentAddTaskBinding, MainViewModel> implements onAddFileCallBack {

    @Inject
    protected IconDrawableLoader iconDrawableLoader;

    private SubTaskAdapter subTaskAdapter;
    private FileAttachmentsAdapter fileAttachmentsAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_add_task;
    }

    @Override
    public void preformViewModel() {
        viewModel = new ViewModelProvider(activity).get(MainViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.addTaskToolbar.setBackground(CommonUtils.getGradientDrawable(
                GradientDrawable.RECTANGLE
                , GradientDrawable.Orientation.TOP_BOTTOM
                , ContextCompat.getColor(activity, R.color.indigo_a_700)
                , ContextCompat.getColor(activity, R.color.indigo_a_400)
                , ContextCompat.getColor(activity, R.color.indigo_a_200)
        ));

        final Calendar calendar = Calendar.getInstance();
        binding.hourNumberPicker.setValue(calendar.get(Calendar.HOUR_OF_DAY));
        binding.minuteNumberPicker.setValue(calendar.get(Calendar.MINUTE));


        binding.addTaskBackIb.setOnClickListener(v -> activity.onBackPressed());
        binding.addTaskIb.setOnClickListener(v -> insertTask());
        binding.addTaskBu.setOnClickListener(v -> insertTask());

        setUpReminderSwitch();
        setUpDateTextViews();
        setUpCategories();

        fileAttachmentsAdapter = new FileAttachmentsAdapter(viewModel.getAddingTask().getValue().task.getAttachments(), this);
        binding.taskAttachmentsRv.setAdapter(fileAttachmentsAdapter);

        subTaskAdapter = new SubTaskAdapter(viewModel.getAddingTask().getValue().subTasks, true);
        binding.taskSubtasksRv.setAdapter(subTaskAdapter);
    }

    private void setUpReminderSwitch() {
        binding.reminderSwitchCompact.setTrackTintList(CommonUtils.getSwitchTrackColorStateList(activity, R.color.orange_a_200));

        binding.reminderSwitchCompact.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.timeLayout.animate().alpha(1.0f).setDuration(500L).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animation) {
                        binding.timeLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animation) {
                    }
                });
            } else {
                binding.timeLayout.animate().alpha(0.0f).setDuration(500L).setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(@NonNull Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(@NonNull Animator animation) {
                        binding.timeLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationCancel(@NonNull Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(@NonNull Animator animation) {
                    }
                });
            }
        });
    }

    private void insertTask() {
        if (!(validateTitle() | validateSubTask() | validateDates())) {
            Date time = viewModel.getAddingTask().getValue().task.getEndDate();

            if (binding.reminderSwitchCompact.isChecked()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    PermissionX.init(activity)
                            .permissions(Manifest.permission.POST_NOTIFICATIONS)
                            .request((allGranted, grantedList, deniedList) -> {
                                        if (allGranted) {
                                            long hour = binding.hourNumberPicker.getValue();
                                            long minute = binding.minuteNumberPicker.getValue();

                                            time.setHours((int) hour);
                                            time.setMinutes((int) minute);
                                            addTask(time);
                                        } else {
                                            Toast.makeText(activity, "You must grant permission.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );
                } else {
                    long hour = binding.hourNumberPicker.getValue();
                    long minute = binding.minuteNumberPicker.getValue();

                    time.setHours((int) hour);
                    time.setMinutes((int) minute);
                    addTask(time);
                }
            } else {
                time.setHours(23);
                time.setMinutes(59);
                time.setSeconds(59);
                addTask(time);
            }
        }
    }

    private void addTask(Date time) {
        if (time.after(new Date())) {
            SubTaskRelation subTaskRelation = viewModel.getAddingTask().getValue();
            Long id = (subTaskRelation.task.getCategory() == null) ? null : subTaskRelation.task.getCategory().getId();

            subTaskRelation.task.setTitle(binding.taskTitleEt.getText().toString());
            subTaskRelation.task.setDescription(binding.taskDescriptionEt.getText().toString());
            subTaskRelation.task.setCategoryId(id);
            subTaskRelation.task.setEndDate(time);

            viewModel.insertTask()
                    .subscribeOn(viewModel.getSchedulerProvider().io())
                    .observeOn(viewModel.getSchedulerProvider().ui())
                    .subscribe(new SingleObserver<Long>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                        }

                        @Override
                        public void onSuccess(Long taskId) {
                            if (taskId > 0) {
                                for (SubTask subTask : subTaskRelation.subTasks) {
                                    subTask.setTaskId(taskId);
                                    viewModel.dataManager.insertSubTask(subTask)
                                            .subscribeOn(viewModel.getSchedulerProvider().io())
                                            .observeOn(viewModel.getSchedulerProvider().ui())
                                            .subscribe();
                                }

                                if (binding.reminderSwitchCompact.isChecked())
                                    subTaskRelation.schedule(activity, taskId);

                                viewModel.resetAddingTask();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }
                    });

            activity.onBackPressed();
        } else {
            Toast.makeText(activity, "End time must be after current time", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateTitle() {
        final String title = binding.taskTitleEt.getText().toString();
        if (TextUtils.isEmpty(title)) {
            binding.taskTitleEt.setError("Enter title");
            return true;
        }
        return false;
    }

    private boolean validateSubTask() {
        List<SubTask> subTasks = viewModel.getAddingTask().getValue().subTasks;
        subTasks.removeAll(Collections.singleton(null));
        for (SubTask subTask : subTasks) {
            if (subTask != null && TextUtils.isEmpty(subTask.getTitle())) {
                Toast.makeText(activity, "Subtasks title can not be null", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return false;
    }

    private boolean validateDates() {
        Date startDate = viewModel.getAddingTask().getValue().task.getStartDate();
        Date endDate = viewModel.getAddingTask().getValue().task.getEndDate();
        endDate.setHours(23);
        endDate.setMinutes(59);
        endDate.setSeconds(59);
        if (DateUtils.isEqual(startDate, endDate) || endDate.after(startDate)) {
            return false;
        }
        Toast.makeText(activity, "end date must be after start date", Toast.LENGTH_SHORT).show();
        return true;
    }

    @SuppressLint("SetTextI18n")
    private void setUpDateTextViews() {
        Calendar calendar = Calendar.getInstance();


        if (viewModel.getAddingTask().getValue().task.getStartDate() == null || viewModel.getAddingTask().getValue().task.getEndDate() == null) {
            binding.startDateTv.setText(DateTimeUtils.formatWithPattern(calendar.getTime(), DateUtils.TIME_FORMAT));
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            viewModel.getAddingTask().getValue().task.setStartDate(calendar.getTime());
            calendar.add(Calendar.WEEK_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            binding.endDateTv.setText(DateTimeUtils.formatWithPattern(calendar.getTime(), DateUtils.TIME_FORMAT));
            viewModel.getAddingTask().getValue().task.setEndDate(calendar.getTime());
        } else {
            binding.startDateTv.setText(DateTimeUtils.formatWithPattern(viewModel.getAddingTask().getValue().task.getStartDate(), DateUtils.TIME_FORMAT));
            binding.endDateTv.setText(DateTimeUtils.formatWithPattern(viewModel.getAddingTask().getValue().task.getEndDate(), DateUtils.TIME_FORMAT));
        }


        binding.startDateLayout.setOnClickListener(view -> {
            Date date = viewModel.getAddingTask().getValue().task.getStartDate();

            if (date != null) {
                showCalendarDialog(view, new Date(), date);
            }
        });

        binding.endDateLayout.setOnClickListener(view -> {
            Date date = viewModel.getAddingTask().getValue().task.getEndDate();
            Date startDate = viewModel.getAddingTask().getValue().task.getStartDate();

            calendar.setTime(startDate);

            if (date != null) {
                showCalendarDialog(view, calendar.getTime(), date);
            }
        });
    }

    @SuppressLint("CheckResult")
    private void setUpCategories() {
        viewModel.observeAllCategories().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    binding.categoryProgressbar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:

                    binding.categoriesChipGroup.removeAllViews();

                    binding.categoryProgressbar.setVisibility(View.GONE);

                    List<Category> categories = new ArrayList<>(resource.data);


                    final ArrayList<String> tags = new ArrayList<>();
                    tags.add("f");
                    tags.add("s");
                    Icon icon = new Icon(62485, 1, tags
                            , "M19 13H13V19H11V13H5V11H11V5H13V11H19V13Z"
                            , 24, 24, null);
                    iconDrawableLoader.loadDrawable(icon);

                    for (Category category : categories) {
                        iconDrawableLoader.loadDrawable(category.getIcon());
                        binding.categoriesChipGroup.addView(createCategoryChip(category, (compoundButton, checked) -> {
                            if (checked)
                                viewModel.getAddingTask().getValue().task.setCategory(category);
                            else if (viewModel.getAddingTask().getValue().task.getCategory() == category)
                                viewModel.getAddingTask().getValue().task.setCategory(null);
                        }));
                    }

                    Category category = new Category(activity.getResources().getString(R.string.new_category), "#FF000000", icon);

                    categories.add(0, category);
                    binding.categoriesChipGroup.addView(createCategoryChip(category, (compoundButton, checked) -> {
                        if (checked) {
                            binding.categoriesChipGroup.clearCheck();
                            navigate(R.id.action_to_addCategoryFragment);
                        }
                    }), 0);

                    if (categories.size() > 1)
                        ((Chip) binding.categoriesChipGroup.getChildAt(1)).setChecked(true);
                    break;
            }
        });
    }

    private Chip createCategoryChip(Category category, CompoundButton.OnCheckedChangeListener listener) {
        Chip chip = new Chip(activity);

        chip.setId((int) category.getId());
        chip.setText(category.getTitle());
        chip.setCheckable(true);
        chip.setFocusable(true);
        chip.setCheckedIconEnabled(false);
        chip.setChipIcon(category.getIcon().getDrawable());

        chip.setOnCheckedChangeListener(listener);

        float padding = ScreenUtils.toDp(8);
        chip.setChipEndPadding(padding);
        chip.setChipStartPadding(padding);
        chip.setChipMinHeight(ScreenUtils.toDp(40));
        chip.setIconStartPadding(ScreenUtils.toDp(4));


        chip.setChipIconTint(ScreenUtils.getChipStrokeColorStateList(activity.getTheme(), category.getColor()));
        chip.setTextColor(ScreenUtils.getChipStrokeColorStateList(activity.getTheme(), category.getColor()));
        chip.setChipBackgroundColor(ScreenUtils.getChipBackgroundColorStateList(activity.getTheme(), category.getColor()));

        return chip;
    }

    private void showCalendarDialog(View view, Date startDate, Date selectedDate) {
        Dialog dialog = new Dialog(activity);

        CollapsibleCalendar.Companion.setSAVE_STATE(false);

        CollapsibleCalendar collapsibleCalendar = new CollapsibleCalendar(activity);
        collapsibleCalendar.setTextColor(ContextCompat.getColor(activity, R.color.pure_white));
        collapsibleCalendar.setTodayItemTextColor(ContextCompat.getColor(activity, R.color.pure_white));
        collapsibleCalendar.setSelectedItemTextColor(ContextCompat.getColor(activity, R.color.pure_white));
        collapsibleCalendar.setBackground(ContextCompat.getDrawable(activity, R.drawable.all_corner_bg));
        collapsibleCalendar.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.indigo_a_400)));
        collapsibleCalendar.setExpandIconVisible(false);
        collapsibleCalendar.setType(CalenderType.SELECTING_MODE);
        collapsibleCalendar.setSelectedDate(selectedDate);
        collapsibleCalendar.setStartDate(startDate);
        collapsibleCalendar.setCalendarListener(new CollapsibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect(@NonNull Date day) {
                String result = DateUtils.dateToString(DateUtils.TIME_FORMAT, day);
                if (view.getId() == R.id.start_date_layout) {
                    day.setHours(0);
                    day.setMinutes(0);
                    day.setSeconds(0);
                    Date date = viewModel.getAddingTask().getValue().task.getEndDate();
                    if (day.after(date)) {
                        Date endDate = DateTimeUtils.getNextWeekDate(day);
                        endDate.setHours(23);
                        endDate.setMinutes(59);
                        binding.endDateTv.setText(DateTimeUtils.formatWithPattern(endDate, DateUtils.TIME_FORMAT));
                        viewModel.getAddingTask().getValue().task.setEndDate(endDate);
                    }
                    binding.startDateTv.setText(result);
                    viewModel.getAddingTask().getValue().task.setStartDate(day);
                } else if (view.getId() == R.id.end_date_layout) {
                    day.setHours(23);
                    day.setMinutes(59);
                    binding.endDateTv.setText(result);
                    viewModel.getAddingTask().getValue().task.setEndDate(day);
                }
                dialog.dismiss();
            }

            @Override
            public void onItemClick(@NonNull View v) {

            }

            @Override
            public void onDataUpdate() {

            }

            @Override
            public void onMonthChange(int year, int month) {

            }

            @Override
            public void onWeekChange(int position) {

            }

            @Override
            public void onClickListener() {
            }

            @Override
            public void onDayChanged() {
            }
        });

        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, (int) ScreenUtils.toDp(16));
        dialog.getWindow().setBackgroundDrawable(inset);
        dialog.setContentView(collapsibleCalendar);
        dialog.show();
    }

    @Override
    public void addFile(int size) {
        PermissionX.init(activity)
                .permissions(CommonUtils.getPermissionList())
                .request((allGranted, grantedList, deniedList) -> {
                            if (allGranted) {
                                if (size < 10) {
                                    new FilePicker.Builder(activity)
                                            .setLimitItemSelection(10 - size)
                                            .setFileType(FileType.ALL)
                                            .setCancellable(true)
                                            .setGridSpanCount(3)
                                            .setOnSubmitClickListener(files -> {
                                                for (Media media : files) {
                                                    String path = media.getFile().getAbsolutePath();
                                                    fileAttachmentsAdapter.addFile(path);
                                                    viewModel.getAddingTask().getValue().task.getAttachments().add(path);
                                                    binding.taskSubtasksRv.scrollToPosition(subTaskAdapter.getItemCount());
                                                }

                                            })
                                            .setOnItemClickListener((media, pos, adapter) -> {
                                                if (!media.getFile().isDirectory()) {
                                                    adapter.setSelected(pos);
                                                }
                                            })
                                            .buildAndShow();
                                } else
                                    Toast.makeText(requireActivity(), "You can just add 10 files", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(requireActivity(), " READ STORAGE ", Toast.LENGTH_SHORT).show();
                        }
                );
    }
}