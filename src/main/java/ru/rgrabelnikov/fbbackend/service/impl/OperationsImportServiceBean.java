package ru.rgrabelnikov.fbbackend.service.impl;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import ru.rgrabelnikov.fbbackend.domain.CategoryEntity;
import ru.rgrabelnikov.fbbackend.domain.OperationEntity;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationImportDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationImportFileModelDto;
import ru.rgrabelnikov.fbbackend.exception.NotFoundException;
import ru.rgrabelnikov.fbbackend.exception.TaskException;
import ru.rgrabelnikov.fbbackend.mapper.OperationMapper;
import ru.rgrabelnikov.fbbackend.repo.CategoryRepo;
import ru.rgrabelnikov.fbbackend.repo.OperationRepo;
import ru.rgrabelnikov.fbbackend.service.OperationsImportService;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

// TODO переделать на задачи
// IMPORT_FROM_CSV_MONEY_LOVER
@Log4j2
@Service
@RequiredArgsConstructor
public class OperationsImportServiceBean implements OperationsImportService {

    private static final Path UPLOAD_DIR = Path.of("/temp/upload");

    private final OperationRepo repo;
    private final CategoryRepo categoryRepo;

    private final OperationMapper mapper;

    @Override
    public void importOperations(final OperationImportDto meta, final FilePart filePart) {
        log.info("Start importing operations into account {}", meta.accountId());

        // TODO check user, account, categories

        final Path filePath = UPLOAD_DIR.resolve(filePart.filename());
        createParentDirectory(filePath);

        filePart.transferTo(filePath)
                .publishOn(Schedulers.boundedElastic())
                .then(Mono.fromCallable(() -> parseCsv(meta, Files.newInputStream(filePath)))
                        .publishOn(Schedulers.boundedElastic()))
                .flatMap(fileData -> {
                    final List<String> categoryNames = fileData.stream()
                            .map(OperationImportFileModelDto::getCategoryName)
                            .distinct()
                            .toList();
                    final Mono<List<CategoryEntity>> categories = categoryRepo.findByNameIn(categoryNames).collectList();
                    return Mono.zip(Mono.just(fileData), categories);
                })
                .map(tuple -> mapToEntities(tuple, meta))
                .flatMapMany(repo::saveAll)
                .collectList()
                .doOnNext(_ -> deleteFile(filePath))
                .doOnError(_ -> deleteFile(filePath))
                .doOnNext(operations -> log.info("Imported {} operations into account {}", operations.size(), meta.accountId()))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

    private void createParentDirectory(final Path filePath) {
        try {
            final Path createdDirectory = Files.createDirectories(filePath.getParent());
            log.debug("Created directory {}", createdDirectory);
        } catch (final IOException e) {
            throw new TaskException("Could not create temp directory", e);
        }
    }

    private List<OperationImportFileModelDto> parseCsv(final OperationImportDto meta, final InputStream is) {
        try (final Reader reader = new InputStreamReader(is)) {
            final ColumnPositionMappingStrategy<OperationImportFileModelDto> strategy = getCsvMappingStrategy(meta);
            final CsvToBean<OperationImportFileModelDto> csvToBean = new CsvToBeanBuilder<OperationImportFileModelDto>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreEmptyLine(true)
                    .withSkipLines(1)
                    .withSeparator(',')
                    .withQuoteChar('"')
                    .build();
            return csvToBean.parse();
        } catch (final IOException e) {
            throw new TaskException("Could not parse CSV", e);
        }
    }

    private static ColumnPositionMappingStrategy<OperationImportFileModelDto> getCsvMappingStrategy(final OperationImportDto meta) {
        final ColumnPositionMappingStrategy<OperationImportFileModelDto> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(OperationImportFileModelDto.class);

        final String unusedColumnName = "unused";
        final int maxIndex = IntStream.of(
                meta.dateColumnIndex(), meta.amountColumnIndex(), meta.descriptionColumnIndex(), meta.categoryColumnIndex()
        ).max().orElse(0);
        final List<String> list = new ArrayList<>(IntStream.rangeClosed(0, maxIndex).mapToObj(_ -> unusedColumnName).toList());
        list.set(meta.dateColumnIndex(), "date");
        list.set(meta.amountColumnIndex(), "amount");
        list.set(meta.descriptionColumnIndex(), "description");
        list.set(meta.categoryColumnIndex(), "categoryName");
        final String[] columns = list.toArray(new String[0]);
        strategy.setColumnMapping(columns);

        return strategy;
    }

    private List<OperationEntity> mapToEntities(
            final Tuple2<List<OperationImportFileModelDto>, List<CategoryEntity>> tuple, final OperationImportDto meta
    ) {
        final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(meta.dateFormat());
        return tuple.getT1().stream()
                .map(dto -> {
                    final CategoryEntity category = tuple.getT2().stream()
                            .filter(it -> dto.getCategoryName().equals(it.getName()))
                            .filter(it -> {
                                final BigDecimal amount = new BigDecimal(dto.getAmount().replaceAll("[\\s ]", ""));
                                final boolean income = amount.compareTo(new BigDecimal(0)) > 0;
                                return it.getIncome().equals(income);
                            })
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException(CategoryEntity.class, "name = " + dto.getCategoryName()));
                    return mapper.toEntity(dto, category.getId(), dateFormatter, meta.accountId());
                })
                .toList();
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
