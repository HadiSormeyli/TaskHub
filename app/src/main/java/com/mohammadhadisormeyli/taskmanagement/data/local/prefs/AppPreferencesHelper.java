package com.mohammadhadisormeyli.taskmanagement.data.local.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.mohammadhadisormeyli.taskmanagement.BuildConfig;

import javax.inject.Inject;

public class AppPreferencesHelper implements PreferencesHelper {

    private static final String NIGHT_MODE = "NIGHT_MODE";
    private static final String REMINDER = "REMINDER";
    private static final String USER = "USER";
    private final SharedPreferences sharedPreferences;

    @Inject
    public AppPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(BuildConfig.PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public int getNightMode() {
        return sharedPreferences.getInt(NIGHT_MODE, 2);
    }

    @Override
    public void setNightMode(int isNightMode) {
        sharedPreferences.edit().putInt(NIGHT_MODE, isNightMode).apply();
    }

    @Override
    public boolean getReminder() {
        return sharedPreferences.getBoolean(REMINDER, true);
    }

    @Override
    public void setReminder(boolean reminder) {
        sharedPreferences.edit().putBoolean(REMINDER, reminder).apply();
    }

    @Override
    public void insertUser() {
        sharedPreferences.edit().putBoolean(USER, true).apply();
    }

    @Override
    public boolean checkUser() {
        return sharedPreferences.getBoolean(USER, false);
    }
}
