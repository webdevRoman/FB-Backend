package ru.rgrabelnikov.fbbackend.dto.operation;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class OperationExportFileModelDto {
    private String date;
    private BigDecimal amount;
    private String description;
    private Boolean income;
    private UUID categoryId;
    private String categoryName;
}
