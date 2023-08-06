package com.mohammadhadisormeyli.taskmanagement.di.module;

import android.app.Application;
import android.content.Context;

import com.maltaisn.icondialog.pack.IconDrawableLoader;
import com.mohammadhadisormeyli.taskmanagement.data.AppDataManager;
import com.mohammadhadisormeyli.taskmanagement.data.DataManager;
import com.mohammadhadisormeyli.taskmanagement.data.local.prefs.AppPreferencesHelper;
import com.mohammadhadisormeyli.taskmanagement.data.local.prefs.PreferencesHelper;
import com.mohammadhadisormeyli.taskmanagement.utils.rx.AppSchedulerProvider;
import com.mohammadhadisormeyli.taskmanagement.utils.rx.SchedulerProvider;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

    @Provides
    @Singleton
    DataManager provideDataManager(AppDataManager appDataManager) {
        return appDataManager;
    }

    @Provides
    @Singleton
    PreferencesHelper providePreferencesHelper(AppPreferencesHelper appPreferencesHelper) {
        return appPreferencesHelper;
    }

    @Provides
    SchedulerProvider provideSchedulerProvider() {
        return new AppSchedulerProvider();
    }

    @Singleton
    @Provides
    IconDrawableLoader provideIconDrawableLoader(Context context) {
        return new IconDrawableLoader(context);
    }
}
