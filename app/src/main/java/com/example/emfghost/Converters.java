package com.example.emfghost;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    @TypeConverter
    public static String fromReadingList(List<EMFReading> readings) {
        if (readings == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<EMFReading>>() {}.getType();
        return gson.toJson(readings, type);
    }

    @TypeConverter
    public static List<EMFReading> toReadingList(String readingsString) {
        if (readingsString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<EMFReading>>() {}.getType();
        return gson.fromJson(readingsString, type);
    }
}

