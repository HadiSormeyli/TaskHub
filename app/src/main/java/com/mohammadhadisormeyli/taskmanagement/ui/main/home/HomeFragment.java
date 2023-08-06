package com.mohammadhadisormeyli.taskmanagement.ui.main.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.maltaisn.icondialog.pack.IconDrawableLoader;
import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.databinding.FragmentHomeBinding;
import com.mohammadhadisormeyli.taskmanagement.model.SubTaskRelation;
import com.mohammadhadisormeyli.taskmanagement.model.Task;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseFragment;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.calendar.view.OnSwipeTouchListener;
import com.mohammadhadisormeyli.taskmanagement.ui.custom.calendar.widget.CollapsibleCalendar;
import com.mohammadhadisormeyli.taskmanagement.ui.main.MainActivity;
import com.mohammadhadisormeyli.taskmanagement.ui.main.home.adapter.TaskAdapter;
import com.mohammadhadisormeyli.taskmanagement.ui.main.task.callback.OnTaskClickCallBack;
import com.mohammadhadisormeyli.taskmanagement.utils.DateUtils;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.CalenderDateState;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class HomeFragment extends BaseFragment<FragmentHomeBinding, MainViewModel> implements CollapsibleCalendar.CalendarListener, OnTaskClickCallBack {

    @Inject
    protected IconDrawableLoader iconDrawableLoader;

    private TaskAdapter taskAdapter;

    public HomeFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void preformViewModel() {
        viewModel = new ViewModelProvider(activity).get(MainViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpCalendar();
        setUpTaskRv();

        observeData();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUpTaskRv() {
        binding.homeTaskRv.setOnTouchListener((view1, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                if (binding.collapsibleCalendarView.getExpanded()) {
                    binding.collapsibleCalendarView.collapse(300);
                }
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (!view1.canScrollVertically(-1)) {
                    binding.collapsibleCalendarView.expand(300);
                } else if (binding.collapsibleCalendarView.getExpanded()) {
                    binding.collapsibleCalendarView.collapse(300);
                }
            }
            return false;
        });

        binding.homeTaskRv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    ((MainActivity) activity).scrollBottomAppBarToTop();
                }
            }
        });

        binding.homeTaskRv.setLayoutManager(new LinearLayoutManager(activity));
    }

    private void observeData() {
//        IconDrawableLoader iconDrawableLoader = new IconDrawableLoader(activity);

        viewModel.observeDateState().observe(getViewLifecycleOwner(), date -> {
            viewModel.loadTasks(date.getYear(), date.getMonth());
        });
        taskAdapter = new TaskAdapter(iconDrawableLoader, HomeFragment.this);
        binding.homeTaskRv.setAdapter(taskAdapter);

        viewModel.observeTasks().observe(getViewLifecycleOwner(), resource -> {
            binding.collapsibleCalendarView.clearEvents();
            switch (resource.status) {
                case LOADING:
                    binding.homeTasksProgressbar.setVisibility(View.VISIBLE);
                    binding.noTaskTv.setVisibility(View.GONE);
                    break;
                case ERROR:
                    binding.homeTasksProgressbar.setVisibility(View.GONE);
                    binding.noTaskTv.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    binding.homeTasksProgressbar.setVisibility(View.GONE);
                    List<SubTaskRelation> tasks = resource.data;
                    ArrayList<Object> arrayList = new ArrayList<>(tasks);

                    if (!tasks.isEmpty()) {
                        binding.noTaskTv.setVisibility(View.GONE);
                        int i = 0;
                        do {
                            Task task = tasks.get(i).task;
                            binding.collapsibleCalendarView.addEventTag(task.getStartDate()
                                    , (task.getCategory() != null) ? task.getCategory().getColor() : "#fafafa");
                            if (i == 0 || !DateUtils.isEqual(task.getStartDate(), tasks.get(i - 1).task.getStartDate())) {
                                arrayList.add(i + (arrayList.size() - tasks.size())
                                        , DateUtils.dateToString("MMM, dd", task.getStartDate()));
                            }
                            i++;
                        } while (i < tasks.size());
                    } else {
                        binding.noTaskTv.setVisibility(View.VISIBLE);
                    }

                    binding.collapsibleCalendarView.reload();
                    taskAdapter.submitList(new ArrayList<>(arrayList));
                    break;
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setUpCalendar() {

        binding.collapsibleCalendarView.saveUIState(true);

        binding.collapsibleCalendarView.setCalendarListener(this);

        binding.collapsibleCalendarView.setOnTouchListener(new OnSwipeTouchListener(activity) {
            @Override
            public void onSwipeRight() {
                binding.collapsibleCalendarView.nextDay();
            }

            @Override
            public void onSwipeLeft() {
                binding.collapsibleCalendarView.prevDay();
            }

            @Override
            public void onSwipeTop() {
                if (binding.collapsibleCalendarView.getExpanded()) {
                    binding.collapsibleCalendarView.collapse(300);
                }
            }

            @Override
            public void onSwipeBottom() {
                if (!binding.collapsibleCalendarView.getExpanded()) {
                    binding.collapsibleCalendarView.expand(300);
                }
            }
        });
    }

    @Override
    public void onDaySelect(@NonNull Date day) {
    }

    @Override
    public void onItemClick(@NonNull View v) {
    }

    @Override
    public void onDataUpdate() {
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onMonthChange(int year, int month) {
        ((MainActivity) activity).scrollBottomAppBarToTop();
        viewModel.setDateState(new CalenderDateState(year, month));
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

    @Override
    public void onTaskClick(SubTaskRelation subTaskRelation) {
        viewModel.setSelectedTask(subTaskRelation);
        navigate(R.id.action_to_taskDetailsFragment);
    }

    @Override
    public void onTaskDelete(SubTaskRelation subTaskRelation) {
        subTaskRelation.cancelAlarm(activity);
        viewModel.delete(subTaskRelation.task);
    }

    @Override
    public void onTaskEdit(SubTaskRelation subTaskRelation) {
    }
}