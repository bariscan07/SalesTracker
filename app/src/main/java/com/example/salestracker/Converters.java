package com.example.salestracker;

import androidx.room.TypeConverter;

import java.util.Date;


//This class provides conversion between date and string to avoid errors during database commits.
public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}