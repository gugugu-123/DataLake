package com.example.demo.common.processor.Impl;

import com.example.demo.common.processor.SqlProcessor;

import java.util.ArrayList;
import java.util.List;

public class UpdateSqlProcessor implements SqlProcessor {
    @Override
    public List<String> getTable(String sql) {
        List<String> tableList = new ArrayList<>();
        String[] s = sql.split(" ");
        for (int i = 0; i < s.length; i++) {
            if ("update".equals(s[i].toLowerCase().trim())) {
                tableList.add(s[i + 1].trim());
                return tableList;
            }
        }
        return tableList;
    }

    @Override
    public List<String> getColumn(String sql) {
        return null;
    }
}
