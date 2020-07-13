package com.nasnav.services.api;

import com.nasnav.model.DataInfo;

import java.io.InputStream;

public interface ExcelParser {

    DataInfo parseExcelToList(InputStream inputStream);
}
