package ru.rgrabelnikov.fbbackend.service;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.dto.ListWrapperDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationCreateDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationExportDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationImportDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationUpdateDto;

import java.time.LocalDate;
import java.util.UUID;

public interface OperationService {

    Mono<ListWrapperDto<OperationDto>> getAllAccountOperations(UUID accountId, LocalDate dateFrom, LocalDate dateTo);

    Mono<OperationDto> createOperation(OperationCreateDto body);

    Mono<OperationDto> updateOperation(UUID id, OperationUpdateDto body);

    Mono<Void> deleteOperation(UUID id);

    Mono<Void> importOperations(OperationImportDto meta, FilePart file);

    Flux<DataBuffer> exportOperations(OperationExportDto body, String filename);
}
