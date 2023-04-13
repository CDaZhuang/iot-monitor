package org.cdaz.representation.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class JsonUtils {
    private static final Gson gson = new Gson();
    public static Object fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }

    public static String toJson(Object o) {
        return gson.toJson(o);
    }
}
