package org.cdaz.mq.util;

import com.google.gson.Gson;
import org.apache.http.util.ByteArrayBuffer;

import java.io.IOException;
import java.io.InputStream;

public class JsonUtils<T> {
    private static final Gson gson = new Gson();

    private static final int CACHE_CAPACITY = 8192;
    private static final byte[] CACHE = new byte[CACHE_CAPACITY];

    public static <T> T fromJson(InputStream inputStream, Class<T> clazz) {
        int off = 0;
        ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(CACHE_CAPACITY);
        try {
            int readbyteCnt = 0;
            while ((readbyteCnt = inputStream.read(CACHE, off, CACHE_CAPACITY)) != -1) {
                byteArrayBuffer.append(CACHE, off, readbyteCnt);
                off += readbyteCnt;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        System.out.println(new String(byteArrayBuffer.toByteArray()));

        // byteArrayBuffer.setLength(off);
        return gson.fromJson(new String(byteArrayBuffer.toByteArray()), clazz);
    }

    public static String toJson(Object o) {
        return gson.toJson(o);
    }

}
