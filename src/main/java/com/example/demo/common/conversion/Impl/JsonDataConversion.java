package com.example.demo.common.conversion.Impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.demo.common.conversion.DataFormatConversion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonDataConversion implements DataFormatConversion {

    @Override
    public String format(String json) {
        String formatData = json.replace("\\", "").replace("\"", "");
        List<JSONObject> formatResultTable = new ArrayList<>();
        JSONArray jsonArray = JSONUtil.parseArray(formatData);
        for (Object valueJson : jsonArray) {
            JSONObject value = JSONUtil.parseObj(valueJson);
            formatResultTable.add((JSONUtil.parseObj(value.get("value"))));
        }
        return JSONUtil.toJsonStr(formatResultTable);
    }

    @Override
    public String formatToOrigin(String json) {
        StringBuffer buffer = new StringBuffer();
        String formatData = json.replace("\\", "").replace("\"", "");
        JSONArray jsonArray = JSONUtil.parseArray(formatData);
        for (Object valueJson : jsonArray) {
            JSONObject valueObject = JSONUtil.parseObj(valueJson);
            JSONObject value = JSONUtil.parseObj(valueObject.get("value"));
            buffer.append(value.toString());
            buffer.append(",");
        }
        if (buffer.length() != 0) {
            buffer.deleteCharAt(buffer.length() - 1);
        }
        String result = "";
        if (jsonArray.size() > 1) {
            buffer.append("]");
            result = "[";
        }
        return result + buffer.toString();
    }

    @Override
    public List<String> getColumn(String json) {
        List<String> column = new ArrayList<>();

        String formatData = json.replace("\\", "").replace("\"", "");
        JSONArray jsonArray = JSONUtil.parseArray(formatData);
        for (Object valueJson : jsonArray) {
            JSONObject valueObject = JSONUtil.parseObj(valueJson);
            JSONObject value = JSONUtil.parseObj(valueObject.get("value"));
            for (Map.Entry<String, Object> objectEntry : value) {
                column.add(objectEntry.getKey());
            }
            break;
        }
        return column;
    }
}
