package com.example.silti;

import androidx.room.TypeConverter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Converters {

    // ========== Date Converters ==========
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    // ========== List<String> Converters ==========
    @TypeConverter
    public static String fromStringList(List<String> list) {
        if (list == null || list.isEmpty()) return null;
        StringBuilder sb = new StringBuilder();
        for (String item : list) {
            if (sb.length() > 0) sb.append(",");
            sb.append(item);
        }
        return sb.toString();
    }

    @TypeConverter
    public static List<String> toStringList(String data) {
        if (data == null || data.isEmpty()) return null;
        return Arrays.asList(data.split(","));
    }

    // ========== List<Integer> Converters ==========
    @TypeConverter
    public static String fromIntegerList(List<Integer> list) {
        if (list == null || list.isEmpty()) return null;
        StringBuilder sb = new StringBuilder();
        for (Integer item : list) {
            if (sb.length() > 0) sb.append(",");
            sb.append(item);
        }
        return sb.toString();
    }

    @TypeConverter
    public static List<Integer> toIntegerList(String data) {
        if (data == null || data.isEmpty()) return null;
        String[] items = data.split(",");
        Integer[] result = new Integer[items.length];
        for (int i = 0; i < items.length; i++) {
            result[i] = Integer.parseInt(items[i].trim());
        }
        return Arrays.asList(result);
    }

    // ========== Boolean Converters ==========
    @TypeConverter
    public static int booleanToInt(boolean value) {
        return value ? 1 : 0;
    }

    @TypeConverter
    public static boolean intToBoolean(int value) {
        return value == 1;
    }
}
