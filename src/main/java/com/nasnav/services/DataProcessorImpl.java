package com.nasnav.services;

import com.nasnav.ErrorMessage;
import com.nasnav.InMemory;
import com.nasnav.model.ColumnEnum;
import com.nasnav.model.DataInfo;
import com.nasnav.services.api.DataProcessor;
import com.nasnav.services.api.ExcelParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DataProcessorImpl implements DataProcessor {

    @Autowired
    private ExcelParser excelParser;

    @Override
    public String uploadXlsx(MultipartFile multipartFile) {
        try {
            //Parse the Excel file into list of DataProcess POJO class
            final String extension = Optional.ofNullable(multipartFile.getOriginalFilename())
                    .filter(f -> f.contains(".")).map(f -> f.substring(multipartFile.getOriginalFilename()
                            .lastIndexOf(".") + 1)).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            String.format(ErrorMessage.FILE_NO_EXTENSION, multipartFile.getOriginalFilename())));

            DataInfo dataProcesses;
            if (extension.equalsIgnoreCase("XLSX") || extension.equalsIgnoreCase("XLS")) {
                dataProcesses = excelParser.parseExcelToList(multipartFile.getInputStream());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(ErrorMessage.UPLOADED_FILE_TYPE, multipartFile.getOriginalFilename()));
            }

            //Generate the UUID based on the file original name
            final String uuid = UUID.nameUUIDFromBytes(multipartFile.getOriginalFilename().getBytes()).toString();

            InMemory.getDataProcess().put(uuid, dataProcesses);
            return uuid;
        } catch (IOException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ErrorMessage.ERROR_UPLOADING_PARSING);
        }
    }

    @Override
    public File returnCsv(String uuid) {
        if (!InMemory.getDataProcess().containsKey(uuid)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(ErrorMessage.KEY_NOT_FOUND_IN_MAP, uuid));
        }

        return generateCsvFromList(uuid);
    }

    @Override
    public File assignColumn(String uuid, String name, Integer index) {
        if (!InMemory.getDataProcess().containsKey(uuid)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(ErrorMessage.KEY_NOT_FOUND_IN_MAP, uuid));
        }

        final DataInfo dataInfo = InMemory.getDataProcess().get(uuid);
        ColumnEnum columnEnum;
        try {
            columnEnum = ColumnEnum.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format(ErrorMessage.INVALID_VALUE_ENUM, name));
        }
        if (dataInfo.isHasHeader()) {
            dataInfo.getData().get(0)[index-1] = columnEnum.name();
        } else {
            String[] header = new String[dataInfo.getData().get(0).length];
            header[index-1] = columnEnum.name();

            dataInfo.getData().add(0, header);
            dataInfo.setHasHeader(true);
        }

        return generateCsvFromList(uuid);
    }

    private File generateCsvFromList(String uuid) {
        File file = new File(String.format("generated_by_%s.csv", uuid));
        // Creating writer class to generate csv file
        try {
            FileWriter writer = new FileWriter(file.getName());
            final List<String[]> list = InMemory.getDataProcess().get(uuid).getData();
            for (String[] arr : list) {
                String collect = String.join(",", arr);
                writer.write(collect);
                writer.write("\n"); // newline
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
