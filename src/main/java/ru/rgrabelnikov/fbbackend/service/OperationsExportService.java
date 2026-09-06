package ru.rgrabelnikov.fbbackend.service;

import org.springframework.core.io.buffer.DataBuffer;
import reactor.core.publisher.Flux;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationExportDto;

public interface OperationsExportService {

    Flux<DataBuffer> exportOperations(OperationExportDto body, String filename);
}
