package ru.rgrabelnikov.fbbackend.service.impl;

import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import ru.rgrabelnikov.fbbackend.domain.CategoryEntity;
import ru.rgrabelnikov.fbbackend.domain.OperationEntity;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationExportDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationExportFileModelDto;
import ru.rgrabelnikov.fbbackend.exception.TaskException;
import ru.rgrabelnikov.fbbackend.mapper.OperationMapper;
import ru.rgrabelnikov.fbbackend.repo.CategoryRepo;
import ru.rgrabelnikov.fbbackend.repo.OperationRepo;
import ru.rgrabelnikov.fbbackend.service.OperationsExportService;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toMap;

@Log4j2
@Service
@RequiredArgsConstructor
public class OperationsExportServiceBean implements OperationsExportService {

    private static final Path DOWNLOAD_DIR = Path.of("/temp/download");

    private final OperationRepo repo;
    private final CategoryRepo categoryRepo;

    private final OperationMapper mapper;

    @Override
    public Flux<DataBuffer> exportOperations(final OperationExportDto body, final String filename) {
        log.info("Start exporting operations from account {}", body.accountId());

        // TODO check user, account, categories

        final Path filePath = DOWNLOAD_DIR.resolve(filename);
        createParentDirectory(filePath);

        return repo.findByAccountIdOrderByDate(body.accountId())
                .collectList()
                .flatMap(operations -> {
                    final List<UUID> categoryIds = operations.stream()
                            .map(OperationEntity::getCategoryId)
                            .toList();
                    final Mono<List<CategoryEntity>> categories = categoryRepo.findByIdIn(categoryIds).collectList();
                    return Mono.zip(Mono.just(operations), categories);
                })
                .map(this::mapToModels)
                .doOnNext(models -> writeCsv(models, filePath))
                .flatMapMany(_ -> DataBufferUtils.read(filePath, new DefaultDataBufferFactory(), 4096))
                .doOnError(_ -> deleteFile(filePath))
                .doOnComplete(() -> {
                    deleteFile(filePath);
                    log.info("Exported operations from account {}", body.accountId());
                })
                .subscribeOn(Schedulers.boundedElastic());
    }

    private void createParentDirectory(final Path filePath) {
        try {
            final Path createdDirectory = Files.createDirectories(filePath.getParent());
            log.debug("Created directory {}", createdDirectory);
        } catch (final IOException e) {
            throw new TaskException("Could not create temp directory", e);
        }
    }

    private List<OperationExportFileModelDto> mapToModels(final Tuple2<List<OperationEntity>, List<CategoryEntity>> tuple) {
        final Map<UUID, CategoryEntity> categories = tuple.getT2().stream().collect(toMap(CategoryEntity::getId, identity()));
        return tuple.getT1().stream()
                .map(operation -> mapper.toExportModel(operation, categories.get(operation.getCategoryId())))
                .toList();
    }

    private void writeCsv(final List<OperationExportFileModelDto> data, final Path path) {
        try (final Writer writer = new FileWriter(path.toString())) {
            final StatefulBeanToCsv<OperationExportFileModelDto> sbc = new StatefulBeanToCsvBuilder<OperationExportFileModelDto>(writer)
                    .withSeparator(',')
                    .build();
            sbc.write(data);
        } catch (final IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            throw new TaskException("Could not write CSV", e);
        }
    }

    private void deleteFile(final Path filePath) {
        try {
            Files.delete(filePath);
            log.debug("Deleted file {}", filePath);
        } catch (final IOException e) {
            throw new TaskException("Could not delete file", e);
        }
    }
}
