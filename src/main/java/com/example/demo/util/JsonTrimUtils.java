package com.example.demo.util;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import java.util.Iterator;
import java.util.Map;

public final class JsonTrimUtils {
 
    /**
     * 构造器
     */
    private JsonTrimUtils() {
    }
 
    /**
     * 去除json值前后空格
     *
     * @param jsonStr jsonStr
     * @return
     */
    public static JSONObject jsonTrim(String jsonStr) {
        return jsonTrim(JSONUtil.parseObj(jsonStr));
    }
 
    /**
     * 去除value的空格
     *
     * @param jsonObject jsonObject
     * @return
     */
    public static JSONObject jsonTrim(JSONObject jsonObject) {
        Iterator<Map.Entry<String, Object>> iterator = jsonObject.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            Object value = next.getValue();
            if (value != null) {
                if (value instanceof String) {
                    //清空值前后空格
                    jsonObject.put(next.getKey(), ((String) value).trim());
                } else if (value instanceof JSONObject) {
                    jsonTrim((JSONObject) value);
                } else if (value instanceof JSONArray) {
                    jsonTrimArray((JSONArray) value);
                }
            }
        }
 
        return jsonObject;
    }
 
    /**
     * 清空JSONArray 值前后空格
     *
     * @param array
     */
    private static void jsonTrimArray(JSONArray array) {
        if (array.size() > 0) {
            for (int i = 0; i < array.size(); i++) {
                Object object = array.get(i);
                if (object != null) {
                    if (object instanceof String) {
                        array.set(i, ((String) object).trim());
                    } else if (object instanceof JSONObject) {
                        jsonTrim((JSONObject) object);
                    } else if (object instanceof JSONArray) {
                        jsonTrimArray((JSONArray) object);
                    }
                }
            }
        }
    }
}