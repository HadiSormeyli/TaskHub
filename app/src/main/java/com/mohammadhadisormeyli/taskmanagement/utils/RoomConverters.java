package com.mohammadhadisormeyli.taskmanagement.utils;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.maltaisn.icondialog.data.Icon;
import com.mohammadhadisormeyli.taskmanagement.model.Category;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

public final class RoomConverters {


    @TypeConverter
    public static Date fromTimestamp(String value) {
        return value == null ? null :
                DateUtils.stringToDate(DateUtils.ROOM_TIME_FORMAT, value);
    }

    @TypeConverter
    public static String dateToTimestamp(Date date) {
        return date == null ? null :
                DateUtils.dateToString(DateUtils.ROOM_TIME_FORMAT, date);
    }

    @TypeConverter
    public static List<String> getListFromString(String attachments) {
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        return new Gson().fromJson(attachments, listType);
    }

    @TypeConverter
    public static String getStringFromArrayList(List<String> attachments) {
        return new Gson().toJson(attachments);
    }

    @TypeConverter
    public static Category getCategoryFromString(String category) {
        Type listType = new TypeToken<Category>() {
        }.getType();
        return new Gson().fromJson(category, listType);
    }

    @TypeConverter
    public static String getStringFromCategory(Category category) {
        if (category != null)
            category.getIcon().setDrawable$lib_release(null);
        return new Gson().toJson(category);
    }

    @TypeConverter
    public static Icon getIconFromString(String icon) {
        Type type = new TypeToken<Icon>() {
        }.getType();
        return new Gson().fromJson(icon, type);
    }

    @TypeConverter
    public static String getStringFromIcon(Icon icon) {
        icon.setDrawable$lib_release(null);
        return new Gson().toJson(icon);
    }
}
