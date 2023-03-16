package com.example.demo.common.conversion.Impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.demo.common.conversion.DataFormatConversion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PictureDataConversion implements DataFormatConversion {

    @Override
    public String format(String json) {
        JSONArray jsonArray = JSONUtil.parseArray(json);
//        for (Object valueJson : jsonArray) {
//            JSONObject value = JSONUtil.parseObj(valueJson);
//            formatResultTable.add((JSONUtil.parseObj(value.get("value"))));
//        }
        return JSONUtil.toJsonStr(jsonArray);
    }

    @Override
    public String formatToOrigin(String json) {
        JSONArray jsonArray = JSONUtil.parseArray(json);
        Object content = "";
        for (Object valueJson : jsonArray) {
            JSONObject value = JSONUtil.parseObj(valueJson);
            JSONObject valueObject = JSONUtil.parseObj(value.get("value"));
            content = valueObject.get("content");
        }
        return (String) content;
    }

    @Override
    public List<String> getColumn(String json) {
        List<String> column = new ArrayList<>();
        JSONArray jsonArray = JSONUtil.parseArray(json);
        for (Object valueJson : jsonArray) {
            JSONObject value = JSONUtil.parseObj(valueJson);
            JSONObject valueObject = JSONUtil.parseObj(value.get("value"));
            for (Map.Entry<String, Object> objectEntry : valueObject) {
                column.add(objectEntry.getKey());
            }
            break;
        }
        return column;
    }
}
