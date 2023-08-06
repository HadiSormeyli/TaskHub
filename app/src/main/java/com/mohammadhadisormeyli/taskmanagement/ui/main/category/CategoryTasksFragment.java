package com.mohammadhadisormeyli.taskmanagement.ui.main.category;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.databinding.FragmentCategoryTasksBinding;
import com.mohammadhadisormeyli.taskmanagement.model.Category;
import com.mohammadhadisormeyli.taskmanagement.model.SubTaskRelation;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseFragment;
import com.mohammadhadisormeyli.taskmanagement.ui.main.home.adapter.TaskAdapter;
import com.mohammadhadisormeyli.taskmanagement.ui.main.task.callback.OnTaskClickCallBack;
import com.mohammadhadisormeyli.taskmanagement.utils.CommonUtils;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.MainViewModel;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.Sort;

import java.util.ArrayList;


public class CategoryTasksFragment extends BaseFragment<FragmentCategoryTasksBinding, MainViewModel> implements OnTaskClickCallBack {

    private TaskAdapter taskAdapter;

    public CategoryTasksFragment() {
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_category_tasks;
    }

    @Override
    public void preformViewModel() {
        viewModel = new ViewModelProvider(activity).get(MainViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.categoryTasksBackIb.setOnClickListener(v -> activity.onBackPressed());

        observeData();
    }

    private void observeData() {
        viewModel.observeSelectedCategory().observe(getViewLifecycleOwner(), category -> {
            binding.categorySearchIb.setOnClickListener(v -> {
                viewModel.getSearchFilter().setContainsCategories(false);
                viewModel.getSearchFilter().getCategoriesId().add(category.getId());
                try {
                    if (Navigation.findNavController(binding.getRoot()).getBackStackEntry(R.id.searchFragment) != null) {
                        activity.onBackPressed();
                    }
                } catch (Exception e) {
                    navigate(R.id.action_to_searchFragment);
                }
            });
            setUpMenu(category);
            binding.categoryTasksIv.setImageDrawable(category.getIcon().getDrawable());
            binding.categoryTasksTv.setText(category.getTitle());
            binding.categoryTasksToolbar.setBackground(CommonUtils.getBottomOvalDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM
                    , ContextCompat.getColor(activity, R.color.indigo_a_400)
                    , ContextCompat.getColor(activity, R.color.indigo_a_700)
                    , Color.parseColor(category.getColor())));

            viewModel.loadCategoryTasks();
        });

        taskAdapter = new TaskAdapter(null, CategoryTasksFragment.this);
        binding.categoryTasksRv.setLayoutManager(new LinearLayoutManager(activity));
        binding.categoryTasksRv.setAdapter(taskAdapter);
        viewModel.observeCategoriesTask().observe(getViewLifecycleOwner(), resource -> {
            if (resource.isLoading)
                binding.categoryTasksProgressbar.setVisibility(View.VISIBLE);
            else {
                binding.categoryTasksProgressbar.setVisibility(View.GONE);
                taskAdapter.submitList(new ArrayList<>(resource.tasks));
            }
        });
    }

    @SuppressLint({"NonConstantResourceId", "RestrictedApi"})
    private void setUpMenu(Category category) {
        final PopupMenu popup = new PopupMenu(activity, binding.threeDotMenuIb);
        popup.getMenuInflater().inflate(R.menu.category_menu, popup.getMenu());

        Sort sort = viewModel.observeCategoriesTask().getValue().sort;

        popup.getMenu().getItem(1)
                .getSubMenu()
                .getItem(sort.getOrder())
                .setChecked(true);

        popup.getMenu().getItem(1)
                .getSubMenu()
                .add(Menu.NONE, -2, 2, "Sort in")
                .setEnabled(false);

        popup.getMenu().getItem(1)
                .getSubMenu()
                .add(Menu.NONE, -1, 3, "    Reverse")
                .setChecked(sort.isReverse())
                .setCheckable(true);


        popup.setOnMenuItemClickListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.delete_category_item:
                    viewModel.deleteCategory(category);
                    activity.onBackPressed();
                    break;

                case R.id.order_date_item:
                case R.id.order_title_item:
                    menuItem.setChecked(!menuItem.isChecked());

                    viewModel.setTasksSort(new Sort(
                            menuItem.getOrder()
                            , popup.getMenu()
                            .getItem(1)
                            .getSubMenu()
                            .getItem(3)
                            .isChecked()
                    ));
                    viewModel.loadCategoryTasks();
                    keepMenuOpen(menuItem);
                    break;

                case -1:
                    menuItem.setChecked(!menuItem.isChecked());
                    for (int i = 0; i < 2; i++) {
                        if (popup.getMenu()
                                .getItem(1)
                                .getSubMenu()
                                .getItem(i)
                                .isChecked()) {
                            viewModel.setTasksSort(new Sort(
                                    i
                                    , menuItem.isChecked()
                            ));
                            break;
                        }
                    }
                    viewModel.loadCategoryTasks();
                    keepMenuOpen(menuItem);
                    break;

            }
            return false;
        });


        MenuPopupHelper menuHelper = new MenuPopupHelper(activity, (MenuBuilder) popup.getMenu(), binding.threeDotMenuIb);
        menuHelper.setForceShowIcon(true);
        binding.threeDotMenuIb.setOnClickListener(v -> menuHelper.show());
    }

    private void keepMenuOpen(MenuItem menuItem) {
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        menuItem.setActionView(new View(activity));
        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return false;
            }
        });
    }

    @Override
    public void onTaskClick(SubTaskRelation subTaskRelation) {
        viewModel.setSelectedTask(subTaskRelation);
        navigate(R.id.action_to_taskDetailsFragment);
    }

    @Override
    public void onTaskDelete(SubTaskRelation subTaskRelation) {
        subTaskRelation.cancelAlarm(activity);
        viewModel.deleteTask(subTaskRelation.task);
    }

    @Override
    public void onTaskEdit(SubTaskRelation subTaskRelation) {
    }
}