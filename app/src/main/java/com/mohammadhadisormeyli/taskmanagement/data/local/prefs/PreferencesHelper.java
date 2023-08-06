package com.mohammadhadisormeyli.taskmanagement.data.local.prefs;

public interface PreferencesHelper {
    int getNightMode();
    void setNightMode(int isNightMode);

    void setReminder(boolean reminder);
    boolean getReminder();

    void insertUser();
    boolean checkUser();
}
