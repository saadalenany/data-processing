package com.nasnav.services.api;

import com.nasnav.model.Response;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public interface DataProcessor {

    String uploadXlsx(MultipartFile multipartFile);

    File returnCsv(String uuid);

    Response assignColumn(String uuid, String name, Integer index);
}
