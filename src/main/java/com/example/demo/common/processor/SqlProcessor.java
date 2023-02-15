package com.example.demo.common.processor;

import java.util.List;

public interface SqlProcessor {

    List<String> getTable(String sql);

    List<String> getColumn(String sql);
}
