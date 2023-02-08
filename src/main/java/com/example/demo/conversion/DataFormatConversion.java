package com.example.demo.conversion;

import java.util.List;

public interface DataFormatConversion {
    String format(String json);

    String formatToOrigin(String json);

    List<String> getColumn(String json);
}
