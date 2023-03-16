package com.example.demo.common.conversion.Impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.demo.common.conversion.DataFormatConversion;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CsvDataConversion implements DataFormatConversion {

    @Override
    public String format(String json) {

        String formatData = json.replace("\\", "").replace("\"", "");
        JSONArray jsonArray = JSONUtil.parseArray(formatData);
        //JSONObject valueHead = JSONUtil.parseObj(JSONUtil.parseObj(jsonArray.get(0)));
//        for (int i = 1; i < jsonArray.size(); i++) {
//            Object valueJson = jsonArray.get(i);
//            JSONObject valueObject = JSONUtil.parseObj(valueJson);
//            JSONObject value = JSONUtil.parseObj(valueObject.get("value"));
//            JSONObject jsonObject = new JSONObject();
//            for (Map.Entry<String, Object> headEntity : valueHead) {
//                jsonObject.put(headEntity.getValue().toString(), value.get(headEntity.getKey()));
//            }
//            formatResultTable.add(jsonObject);
//        }
        return jsonArray.toString();

    }

    @Override
    public String formatToOrigin(String json) {
        String formatData = json.replace("\\", "").replace("\"", "");
        JSONArray jsonArray = JSONUtil.parseArray(formatData);
        StringBuffer buffer = new StringBuffer();
        for (Object valueJson : jsonArray) {
            JSONObject valueObject = JSONUtil.parseObj(valueJson);
            JSONObject value = JSONUtil.parseObj(valueObject.get("value"));
            String key = "_c";
            for (int i = 0; i < value.size(); i++) {
                buffer.append(value.get(key + i)).append(",");
            }
            if (buffer.length() != 0) {
                buffer.deleteCharAt(buffer.length() - 1);
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }

    @Override
    public List<String> getColumn(String json) {
        List<String> column = new ArrayList<>();
        String formatData = json.replace("\\", "").replace("\"", "");
        JSONArray jsonArray = JSONUtil.parseArray(formatData);
        StringBuffer buffer = new StringBuffer();
        for (Object valueJson : jsonArray) {
            JSONObject valueObject = JSONUtil.parseObj(valueJson);
            JSONObject value = JSONUtil.parseObj(valueObject.get("value"));
            for (Map.Entry<String, Object> objectEntry : value) {
                column.add(objectEntry.getValue().toString());
            }
            break;
        }
        return column;
    }
}
