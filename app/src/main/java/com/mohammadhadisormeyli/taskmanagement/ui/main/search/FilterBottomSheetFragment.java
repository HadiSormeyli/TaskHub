package com.mohammadhadisormeyli.taskmanagement.ui.main.search;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.github.thunder413.datetimeutils.DateTimeUtils;
import com.google.android.material.chip.Chip;
import com.maltaisn.icondialog.pack.IconDrawableLoader;
import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.databinding.FragmentFilterBottomSheetBinding;
import com.mohammadhadisormeyli.taskmanagement.model.Category;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseBottomSheetFragment;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.calendar.widget.CalenderType;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.calendar.widget.CollapsibleCalendar;
import com.mohammadhadisormeyli.taskmanagement.utils.DateUtils;
import com.mohammadhadisormeyli.taskmanagement.utils.ScreenUtils;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.MainViewModel;

import java.util.Date;


public class FilterBottomSheetFragment extends BaseBottomSheetFragment<FragmentFilterBottomSheetBinding, MainViewModel> {


    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_filter_bottom_sheet;
    }

    @Override
    public void preformViewModel() {
        viewModel = new ViewModelProvider(activity).get(MainViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.categoriesCheckBox.setChecked(viewModel.getSearchFilter().isContainsCategories());
        binding.tasksCheckBox.setChecked(viewModel.getSearchFilter().isContainsTasks());

        if (viewModel.getSearchFilter().getSort().getOrder() == 0) {
            binding.titleRadioButton.setChecked(true);
        } else {
            binding.dateRadioButton.setChecked(true);
        }
        binding.reverseCheckBox.setChecked(viewModel.getSearchFilter().getSort().isReverse());


        binding.categoriesCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.getSearchFilter().setContainsCategories(isChecked));

        binding.tasksCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.getSearchFilter().setContainsTasks(isChecked));


        binding.searchRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (R.id.date_radio_button == checkedId) {
                viewModel.getSearchFilter().getSort().setOrder(1);
            } else {
                viewModel.getSearchFilter().getSort().setOrder(0);
            }
        });

        binding.reverseCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.getSearchFilter().getSort().setReverse(isChecked));

        binding.applyFilterBu.setOnClickListener(v -> {
            ((SearchFragment) getParentFragment()).search(viewModel.getSearchFilter().getQuery());
            dismiss();
        });

        binding.removeDateFilterTv.setOnClickListener(v -> {
            viewModel.getSearchFilter().setStartDate(null);
            viewModel.getSearchFilter().setEndDate(null);
            binding.startDateTv.setText("-∞");
            binding.endDateTv.setText("+∞");
            binding.removeDateFilterTv.setVisibility(View.GONE);
        });

        binding.removeCategoryFilterTv.setOnClickListener(v -> {
            viewModel.getSearchFilter().getCategoriesId().clear();
            binding.categoriesChipGroup.clearCheck();
            binding.removeCategoryFilterTv.setVisibility(View.GONE);
        });

        binding.filterBackIb.setOnClickListener(v -> dismiss());

        setUpDateTextViews();
        setUpCategories();
    }

    @SuppressLint("SetTextI18 n")
    private void setUpDateTextViews() {
        if (viewModel.getSearchFilter().getStartDate() != null) {
            binding.removeDateFilterTv.setVisibility(View.VISIBLE);
            binding.startDateTv.setText(viewModel.getSearchFilter().getStartDate());
        }
        if (viewModel.getSearchFilter().getEndDate() != null) {
            binding.removeDateFilterTv.setVisibility(View.VISIBLE);
            binding.endDateTv.setText(viewModel.getSearchFilter().getEndDate());
        }


        binding.startDateLayout.setOnClickListener(view -> {
            Date date = DateUtils.stringToDate(DateUtils.TIME_FORMAT, binding.startDateTv.getText().toString());
            showCalendarDialog(view, null, date);
        });

        binding.endDateLayout.setOnClickListener(view -> {
            Date date = DateUtils.stringToDate(DateUtils.TIME_FORMAT, binding.endDateTv.getText().toString());
            Date startDate = DateUtils.stringToDate(DateUtils.TIME_FORMAT, binding.startDateTv.getText().toString());

            showCalendarDialog(view, startDate, date);
        });
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
        if (selectedDate != null) collapsibleCalendar.setSelectedDate(selectedDate);
        if (startDate != null) collapsibleCalendar.setStartDate(startDate);
        collapsibleCalendar.setCalendarListener(new CollapsibleCalendar.CalendarListener() {
            @Override
            public void onDaySelect(@NonNull Date day) {
                String result;
                if (view.getId() == R.id.start_date_layout) {
                    Date date = DateUtils.stringToDate(DateUtils.TIME_FORMAT, binding.endDateTv.getText().toString());
                    day.setHours(0);
                    day.setMinutes(0);
                    day.setDate(day.getDate() + 1);
                    if (date != null)
                        if (day.after(date)) {
                            Date end = DateTimeUtils.getNextWeekDate(day);
                            end.setHours(23);
                            end.setMinutes(59);
                            end.setSeconds(59);
                            String endDate = DateTimeUtils.formatWithPattern(end, DateUtils.TIME_FORMAT);
                            binding.endDateTv.setText(endDate);
                            viewModel.getSearchFilter().setEndDate(endDate);
                        }

                    result = DateTimeUtils.formatWithPattern(day, DateUtils.TIME_FORMAT);
                    binding.startDateTv.setText(result);
                    viewModel.getSearchFilter().setStartDate(result);
                } else if (view.getId() == R.id.end_date_layout) {
                    day.setHours(23);
                    day.setMinutes(59);
                    day.setSeconds(59);
                    result = DateTimeUtils.formatWithPattern(day, DateUtils.TIME_FORMAT);
                    binding.endDateTv.setText(result);
                    viewModel.getSearchFilter().setEndDate(result);
                }
                binding.removeDateFilterTv.setVisibility(View.VISIBLE);
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

    @SuppressLint("CheckResult")
    private void setUpCategories() {
        IconDrawableLoader iconDrawableLoader = new IconDrawableLoader(activity);
        compositeDisposable.add(viewModel.dataManager.getAllCategories()
                .subscribeOn(viewModel.getSchedulerProvider().io())
                .observeOn(viewModel.getSchedulerProvider().ui())
                .subscribe(
                        categories -> {
                            binding.categoryProgressbar.setVisibility(View.GONE);
                            if (!categories.isEmpty()) {
                                for (Category category : categories) {
                                    iconDrawableLoader.loadDrawable(category.getIcon());
                                    Chip chip = new Chip(activity);

                                    chip.setId((int) category.getId());
                                    chip.setText(category.getTitle());
                                    chip.setCheckable(true);
                                    chip.setFocusable(true);
                                    chip.setCheckedIconEnabled(false);
                                    chip.setChipIcon(category.getIcon().getDrawable());


                                    chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                                        if (isChecked) {
                                            if (!viewModel.getSearchFilter().getCategoriesId().contains(category.getId())) {
                                                if (binding.removeCategoryFilterTv.getVisibility() == View.GONE)
                                                    binding.removeCategoryFilterTv.setVisibility(View.VISIBLE);
                                                viewModel.getSearchFilter().getCategoriesId().add(category.getId());
                                            }
                                        } else {
                                            viewModel.getSearchFilter().getCategoriesId().remove((Object) category.getId());
                                            if (viewModel.getSearchFilter().getCategoriesId().size() == 0) {
                                                binding.removeCategoryFilterTv.setVisibility(View.GONE);
                                            }
                                        }
                                    });

                                    float padding = ScreenUtils.toDp(8);
                                    chip.setChipEndPadding(padding);
                                    chip.setChipStartPadding(padding);
                                    chip.setChipMinHeight(ScreenUtils.toDp(40));
                                    chip.setIconStartPadding(ScreenUtils.toDp(4));


                                    chip.setChipIconTint(ScreenUtils.getChipStrokeColorStateList(activity.getTheme(), category.getColor()));
                                    chip.setTextColor(ScreenUtils.getChipStrokeColorStateList(activity.getTheme(), category.getColor()));
                                    chip.setChipBackgroundColor(ScreenUtils.getChipBackgroundColorStateList(activity.getTheme(), category.getColor()));

                                    binding.categoriesChipGroup.addView(chip);
                                }

                                for (Long id : viewModel.getSearchFilter().getCategoriesId()) {
                                    ((Chip) binding.categoriesChipGroup.findViewById(Math.toIntExact(id))).setChecked(true);
                                }

                                if (viewModel.getSearchFilter().getCategoriesId().size() > 0)
                                    binding.removeCategoryFilterTv.setVisibility(View.VISIBLE);
                            }
                        },
                        throwable -> throwable.printStackTrace()
                ));
    }
}