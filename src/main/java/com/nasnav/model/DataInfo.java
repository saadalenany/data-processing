package com.nasnav.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode
public class DataInfo {

    private List<String[]> data;

    private boolean hasHeader;
}
