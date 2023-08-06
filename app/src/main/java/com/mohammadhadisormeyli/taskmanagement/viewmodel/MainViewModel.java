package com.mohammadhadisormeyli.taskmanagement.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.mohammadhadisormeyli.taskmanagement.data.DataManager;
import com.mohammadhadisormeyli.taskmanagement.model.Category;
import com.mohammadhadisormeyli.taskmanagement.model.SubTask;
import com.mohammadhadisormeyli.taskmanagement.model.SubTaskRelation;
import com.mohammadhadisormeyli.taskmanagement.model.Task;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseViewModel;
import com.mohammadhadisormeyli.taskmanagement.utils.rx.SchedulerProvider;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.FlowableSubscriber;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;

@HiltViewModel
public class MainViewModel extends BaseViewModel {

    private final MutableLiveData<Category> selectedCategory;
    private final MutableLiveData<SubTaskRelation> selectedTask;
    private final SearchFilterState searchFilter;
    private final MutableLiveData<CategoryTasksState> categoryTask;

    private final MutableLiveData<SubTaskRelation> addingTask;

    private final MutableLiveData<Resource<List<Category>>> allCategories;

    private final MutableLiveData<Resource<List<Category>>> searchCategories;
    private final MutableLiveData<Resource<List<SubTaskRelation>>> searchTasks;

    private final MutableLiveData<Resource<List<SubTaskRelation>>> tasks;

    private final MutableLiveData<CalenderDateState> dateState;
    private Disposable disposable;

    @Inject
    public MainViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
        allCategories = new MutableLiveData<>();
        selectedCategory = new MutableLiveData<>();
        selectedTask = new MutableLiveData<>();

        addingTask = new MutableLiveData<>();
        resetAddingTask();

        searchFilter = new SearchFilterState();
        categoryTask = new MutableLiveData<>(new CategoryTasksState());

        searchCategories = new MutableLiveData<>();
        searchTasks = new MutableLiveData<>();

        tasks = new MutableLiveData<>();
        dateState = new MutableLiveData<>(new CalenderDateState());

        loadCategories();
    }

    public MutableLiveData<SubTaskRelation> getSelectedTask() {
        return selectedTask;
    }

    public void setSelectedTask(SubTaskRelation subTaskRelation) {
        selectedTask.setValue(subTaskRelation);
    }

    public void loadTasks(int year, int month) {
        tasks.setValue(Resource.loading());
        if (disposable != null)
            disposable.dispose();

        disposable = dataManager.getTasksByDate(year, month)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(subTaskRelations -> {
                    tasks.setValue(Resource.success(subTaskRelations));
                });
    }

    @Override
    protected void onCleared() {
        if (disposable != null)
            disposable.dispose();
        super.onCleared();
    }

    public MutableLiveData<SubTaskRelation> getAddingTask() {
        return addingTask;
    }

    public MutableLiveData<Resource<List<SubTaskRelation>>> observeTasks() {
        return tasks;
    }

    public MutableLiveData<CalenderDateState> observeDateState() {
        return dateState;
    }

    public void setDateState(CalenderDateState dateState) {
        this.dateState.setValue(dateState);
    }

    public void delete(Task task) {
        dataManager.deleteTask(task)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    private void loadCategories() {
        allCategories.setValue(Resource.loading());

        dataManager.getAllCategories()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(new FlowableSubscriber<List<Category>>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(List<Category> categories) {
                        allCategories.setValue(Resource.success(categories));
                    }

                    @Override
                    public void onError(Throwable t) {
                        allCategories.setValue(Resource.error(t));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    public void loadCategoryTasks() {
        categoryTask.setValue(new CategoryTasksState(true, categoryTask.getValue().sort));
        dataManager.getTasksByCategory(
                        selectedCategory.getValue().getId(),
                        categoryTask.getValue().sort.getOrder(),
                        categoryTask.getValue().sort.isReverse()
                ).subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(new SingleObserver<List<SubTaskRelation>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onSuccess(List<SubTaskRelation> subTaskRelations) {
                        categoryTask.setValue(new CategoryTasksState(
                                false
                                , categoryTask.getValue().sort
                                , subTaskRelations
                        ));
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                });
    }

    public void deleteTask(Task task) {
        dataManager.deleteTask(task)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    public void deleteCategory(Category category) {
        dataManager.deleteCategory(category)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    public void setTasksSort(Sort sort) {
        this.categoryTask.setValue(new CategoryTasksState(sort));
    }

    public MutableLiveData<Resource<List<Category>>> observeAllCategories() {
        return allCategories;
    }

    public MutableLiveData<Category> observeSelectedCategory() {
        return selectedCategory;
    }

    public MutableLiveData<CategoryTasksState> observeCategoriesTask() {
        return categoryTask;
    }

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory.setValue(selectedCategory);
    }

    public SearchFilterState getSearchFilter() {
        return searchFilter;
    }


    public void searchInTasks() {
        searchTasks.setValue(Resource.loading());
        dataManager.searchTask(
                        searchFilter.getQuery(),
                        searchFilter.getCategoriesId(),
                        searchFilter.getStartDate(),
                        searchFilter.getEndDate(),
                        searchFilter.getSort().getOrder(),
                        searchFilter.getSort().isReverse()
                ).subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(new SingleObserver<List<SubTaskRelation>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<SubTaskRelation> subTaskRelations) {
                        searchTasks.setValue(Resource.success(subTaskRelations));
                    }

                    @Override
                    public void onError(Throwable e) {
                        searchTasks.setValue(Resource.error(e));
                    }
                });
    }

    public MutableLiveData<Resource<List<SubTaskRelation>>> observeSearchTasks() {
        return searchTasks;
    }

    public void searchInCategories() {
        searchCategories.setValue(Resource.loading());
        dataManager.searchCategory(
                        searchFilter.getQuery(),
                        searchFilter.getSort().getOrder(),
                        searchFilter.getSort().isReverse()
                ).subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(new SingleObserver<List<Category>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(List<Category> categories) {
                        searchCategories.setValue(Resource.success(categories));
                    }

                    @Override
                    public void onError(Throwable e) {
                        searchCategories.setValue(Resource.error(e));
                    }
                });
    }

    public MutableLiveData<Resource<List<Category>>> observeSearchCategories() {
        return searchCategories;
    }

    public Single<Long> insertTask() {
        SubTaskRelation subTaskRelation = addingTask.getValue();
        return dataManager.insertTask(subTaskRelation.task);
    }

    public void updateSubTasks(SubTask subTask) {
        dataManager.updateSubTask(subTask)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }

    public void resetAddingTask() {
        SubTaskRelation subTaskRelation = new SubTaskRelation();
        subTaskRelation.subTasks = new ArrayList<>();
        subTaskRelation.task = new Task(0L, null, "", "", new ArrayList<>(), null, null);

        addingTask.setValue(subTaskRelation);
    }
}
