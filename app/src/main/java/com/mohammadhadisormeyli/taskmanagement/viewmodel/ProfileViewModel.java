package com.mohammadhadisormeyli.taskmanagement.viewmodel;

import androidx.lifecycle.MutableLiveData;

import com.mohammadhadisormeyli.taskmanagement.data.DataManager;
import com.mohammadhadisormeyli.taskmanagement.model.User;
import com.mohammadhadisormeyli.taskmanagement.ui.base.BaseViewModel;
import com.mohammadhadisormeyli.taskmanagement.utils.rx.SchedulerProvider;

import org.reactivestreams.Subscription;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.FlowableSubscriber;

@HiltViewModel
public class ProfileViewModel extends BaseViewModel {

    private final MutableLiveData<Resource<User>> user;
    private String userAvatar;

    @Inject
    public ProfileViewModel(DataManager dataManager, SchedulerProvider schedulerProvider) {
        super(dataManager, schedulerProvider);
        user = new MutableLiveData<>();
        loadUser();
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    private void loadUser() {
        user.setValue(Resource.loading());
        dataManager.getUser()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(new FlowableSubscriber<User>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Integer.MAX_VALUE);
                    }

                    @Override
                    public void onNext(User u) {
                        user.setValue(Resource.success(u));
                    }

                    @Override
                    public void onError(Throwable t) {
                        user.setValue(Resource.error(t));
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public MutableLiveData<Resource<User>> observeUser() {
        return user;
    }

    public void updateUser(User user) {
        dataManager.updateUser(user)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe();
    }
}
