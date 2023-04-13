package org.cdaz.monitor.provider.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public class JsonUtils {
    private static final Gson gson = new Gson();

    private static final int CACHE_CAPACITY = 4096;
    private static final byte[] CACHE = new byte[CACHE_CAPACITY];

    public static Object fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }

    public static String toJson(Object o) {
        return gson.toJson(o);
    }
}
