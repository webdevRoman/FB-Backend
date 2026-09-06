package ru.rgrabelnikov.fbbackend.dto.operation;

import lombok.Data;

@Data
public class OperationImportFileModelDto {
    private String date;
    private String amount;
    private String description;
    private String categoryName;
}
