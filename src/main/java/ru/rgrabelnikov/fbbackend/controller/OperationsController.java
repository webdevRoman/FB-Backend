package ru.rgrabelnikov.fbbackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.ContentDisposition;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.dto.ListWrapperDto;
import ru.rgrabelnikov.fbbackend.dto.ServerErrorDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationCreateDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationExportDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationImportDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationUpdateDto;
import ru.rgrabelnikov.fbbackend.service.OperationService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/operations", produces = {"application/json"})
@ApiResponse(responseCode = "200", description = "Запрос успешно обработан")
@ApiResponse(responseCode = "400", description = "Ошибка валидации входных данных",
        content = @Content(schema = @Schema(implementation = ServerErrorDto.class)))
@ApiResponse(responseCode = "401", description = "Ошибка авторизации",
        content = @Content(schema = @Schema(implementation = ServerErrorDto.class)))
@ApiResponse(responseCode = "403", description = "Нет полномочий",
        content = @Content(schema = @Schema(implementation = ServerErrorDto.class)))
@ApiResponse(responseCode = "404", description = "Данные не найдены",
        content = @Content(schema = @Schema(implementation = ServerErrorDto.class)))
@ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
        content = @Content(schema = @Schema(implementation = ServerErrorDto.class)))
@Tag(name = "Operations", description = "Работа с операциями")
public class OperationsController {

    private final OperationService service;

    @GetMapping
    @Operation(summary = "Получение операций")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Mono<ListWrapperDto<OperationDto>> getAll(
            @RequestParam("accountId") final UUID accountId,
            @RequestParam("dateFrom") final LocalDate dateFrom,
            @RequestParam("dateTo") final LocalDate dateTo
    ) {
        return service.getAllAccountOperations(accountId, dateFrom, dateTo);
    }

    @PostMapping
    @Operation(summary = "Создание операции")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Mono<OperationDto> create(@Valid @RequestBody final OperationCreateDto body) {
        return service.createOperation(body);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновление операции")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Mono<OperationDto> update(@PathVariable final UUID id, @Valid @RequestBody final OperationUpdateDto body) {
        return service.updateOperation(id, body);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удаление операции")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Mono<Void> delete(@PathVariable final UUID id) {
        return service.deleteOperation(id);
    }

    @PostMapping(path = "/import", consumes = MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Импорт операций")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @ApiResponse(responseCode = "202", description = "Запрос принят")
    public Mono<ResponseEntity<Void>> importOperations(
            @Parameter(content = @Content(mediaType = APPLICATION_JSON_VALUE)) @Valid @RequestPart("meta") final OperationImportDto meta,
            @RequestPart("file") final FilePart file
    ) {
        return service.importOperations(meta, file)
                .thenReturn(ResponseEntity.accepted().build());
    }

    @PostMapping(path = "/export", produces = APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Экспорт операций")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public Flux<DataBuffer> exportOperations(@Valid @RequestBody final OperationExportDto body, final ServerHttpResponse response) {
        final String filename = "operations_%s_%s.csv".formatted(body.accountId(), LocalDate.now().format(DateTimeFormatter.ISO_DATE));
        response.getHeaders().setContentDisposition(ContentDisposition.attachment().filename(filename, UTF_8).build());
        return service.exportOperations(body, filename);
    }
}
