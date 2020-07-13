package com.nasnav;

import com.nasnav.model.DataInfo;

import java.util.HashMap;
import java.util.Map;

public class InMemory {

    private static Map<String, DataInfo> dataProcessList;

    public static Map<String, DataInfo> getDataProcess() {
        if (dataProcessList == null) {
            dataProcessList = new HashMap<>();
        }
        return dataProcessList;
    }
}
