package com.mohammadhadisormeyli.taskmanagement.ui.main.search;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.maltaisn.icondialog.pack.IconDrawableLoader;
import com.mohammadhadisormeyli.taskmanagement.R;
import com.mohammadhadisormeyli.taskmanagement.databinding.FragmentSearchBinding;
import com.mohammadhadisormeyli.taskmanagement.model.Category;
import com.mohammadhadisormeyli.taskmanagement.model.SubTaskRelation;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseFragment;
import com.mohammadhadisormeyli.taskmanagement.ui.main.category.CategoryAdapter;
import com.mohammadhadisormeyli.taskmanagement.ui.main.category.callback.OnCategoryClick;
import com.mohammadhadisormeyli.taskmanagement.ui.main.home.adapter.TaskAdapter;
import com.mohammadhadisormeyli.taskmanagement.ui.main.task.callback.OnTaskClickCallBack;
import com.mohammadhadisormeyli.taskmanagement.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

@AndroidEntryPoint
public class SearchFragment extends BaseFragment<FragmentSearchBinding, MainViewModel> implements OnCategoryClick, OnTaskClickCallBack {

    @Inject
    protected IconDrawableLoader iconDrawableLoader;

    private TaskAdapter taskAdapter;
    private CategoryAdapter categoryAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_search;
    }

    @Override
    public void preformViewModel() {
        viewModel = new ViewModelProvider(activity).get(MainViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        iconDrawableLoader = new IconDrawableLoader(activity);

        binding.searchBackIb.setOnClickListener(v -> activity.onBackPressed());

        binding.filterFabButton.setOnClickListener(v -> {
            new FilterBottomSheetFragment().show(getChildFragmentManager(), "");
        });

        RxSearchObservable.fromView(binding.searchEditText)
                .debounce(500, TimeUnit.MILLISECONDS)
                .map(text -> text.toLowerCase().trim())
                .distinctUntilChanged()
                .switchMap(Observable::just)
                .observeOn(viewModel.getSchedulerProvider().ui())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String query) {
                        search(query);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });

        setUpAdapters();
        observeData();
    }

    private void observeData() {
        viewModel.observeSearchCategories().observe(getViewLifecycleOwner(), resource -> {
            binding.categoriesLayout.setVisibility(View.VISIBLE);
            switch (resource.status) {
                case LOADING:
                case ERROR:
                    binding.categoryProgressbar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    binding.categoryProgressbar.setVisibility(View.GONE);
                    for (Category category : resource.data) {
                        iconDrawableLoader.loadDrawable(category.getIcon());
                    }
                    categoryAdapter.submitList(resource.data);
                    if (resource.data.isEmpty())
                        binding.noCategoriesTv.setVisibility(View.VISIBLE);
                    else binding.noCategoriesTv.setVisibility(View.GONE);
                    break;
            }
        });
        taskAdapter = new TaskAdapter(iconDrawableLoader, SearchFragment.this);
        binding.tasksRecyclerView.setAdapter(taskAdapter);

        viewModel.observeSearchTasks().observe(getViewLifecycleOwner(), resource -> {
            binding.tasksLayout.setVisibility(View.VISIBLE);
            switch (resource.status) {
                case LOADING:
                case ERROR:
                    binding.taskProgressbar.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS:
                    binding.taskProgressbar.setVisibility(View.GONE);
                    taskAdapter.submitList(new ArrayList<>(resource.data));

                    if (resource.data.isEmpty())
                        binding.noTasksTv.setVisibility(View.VISIBLE);
                    else binding.noTasksTv.setVisibility(View.GONE);
                    break;
            }
        });
    }

    private void setUpAdapters() {
        categoryAdapter = new CategoryAdapter(SearchFragment.this);
        binding.categoryRecycleView.setAdapter(categoryAdapter);

        binding.tasksRecyclerView.setLayoutManager(new LinearLayoutManager(activity));
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.showKeyboard(binding.searchEditText);
    }

    public void search(String query) {
        if (!TextUtils.isEmpty(query)) {
            viewModel.getSearchFilter().setQuery(query);
            binding.listLayout.setVisibility(View.VISIBLE);
            if (viewModel.getSearchFilter().isContainsTasks())
                viewModel.searchInTasks();
            else binding.tasksLayout.setVisibility(View.GONE);

            if (viewModel.getSearchFilter().isContainsCategories())
                viewModel.searchInCategories();
            else binding.categoriesLayout.setVisibility(View.GONE);
        } else binding.listLayout.setVisibility(View.GONE);
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

    @Override
    public void onCategoryClick(int position, Category category) {
        viewModel.setSelectedCategory(category);
        navigate(R.id.action_to_categoryTasksFragment);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}