package ru.rgrabelnikov.fbbackend.service;

import org.springframework.http.codec.multipart.FilePart;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationImportDto;

public interface OperationsImportService {

    void importOperations(OperationImportDto meta, FilePart filePart);
}
