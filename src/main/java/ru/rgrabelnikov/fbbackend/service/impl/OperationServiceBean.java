package ru.rgrabelnikov.fbbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.domain.CategoryEntity;
import ru.rgrabelnikov.fbbackend.domain.IconEntity;
import ru.rgrabelnikov.fbbackend.domain.OperationEntity;
import ru.rgrabelnikov.fbbackend.dto.ListWrapperDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationCreateDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationExportDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationImportDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationUpdateDto;
import ru.rgrabelnikov.fbbackend.exception.NotFoundException;
import ru.rgrabelnikov.fbbackend.mapper.CommonMapper;
import ru.rgrabelnikov.fbbackend.mapper.OperationMapper;
import ru.rgrabelnikov.fbbackend.repo.CategoryRepo;
import ru.rgrabelnikov.fbbackend.repo.IconRepo;
import ru.rgrabelnikov.fbbackend.repo.OperationRepo;
import ru.rgrabelnikov.fbbackend.service.OperationService;
import ru.rgrabelnikov.fbbackend.service.OperationsExportService;
import ru.rgrabelnikov.fbbackend.service.OperationsImportService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class OperationServiceBean implements OperationService {

    private final OperationRepo repo;
    private final CategoryRepo categoryRepo;
    private final IconRepo iconRepo;

    private final OperationMapper mapper;
    private final CommonMapper commonMapper;

    private final OperationsImportService importService;
    private final OperationsExportService exportService;

    @Override
    public Mono<ListWrapperDto<OperationDto>> getAllAccountOperations(final UUID accountId, final LocalDate dateFrom, final LocalDate dateTo) {
        // TODO check user account
        final Mono<List<OperationEntity>> operationsMono = repo
                .findByAccountIdAndDateGreaterThanEqualAndDateLessThanEqualOrderByDateDesc(accountId, dateFrom, dateTo)
                .collectList();

        final Mono<Map<UUID, CategoryEntity>> categoriesMono = operationsMono
                .flatMapMany(operations -> {
                    final List<UUID> categoryIds = operations.stream()
                            .map(OperationEntity::getCategoryId)
                            .distinct()
                            .toList();

                    return categoryRepo.findByIdIn(categoryIds);
                })
                .collectMap(CategoryEntity::getId);

        final Mono<Map<UUID, IconEntity>> iconsMono = categoriesMono
                .flatMapMany(categoriesMap -> {
                    final List<UUID> iconIds = categoriesMap.values().stream()
                            .map(CategoryEntity::getIconId)
                            .distinct()
                            .toList();

                    return iconRepo.findByIdIn(iconIds);
                })
                .collectMap(IconEntity::getId);

        return Mono.zip(operationsMono, categoriesMono, iconsMono)
                .map(tuple -> {
                    final List<OperationEntity> operations = tuple.getT1();
                    final Map<UUID, CategoryEntity> categoriesMap = tuple.getT2();
                    final Map<UUID, IconEntity> iconsMap = tuple.getT3();

                    return operations.stream()
                            .map(operation -> mapper.toDto(operation, categoriesMap, iconsMap))
                            .toList();
                })
                .map(commonMapper::toListWrapperDto);
    }

    @Override
    public Mono<OperationDto> createOperation(final OperationCreateDto body) {
        // TODO check user account
        final OperationEntity entity = mapper.toEntity(body);
        return repo.save(entity)
                .doOnNext(operation -> log.info("Created operation: {}", operation.getId()))
                .flatMap(operation -> categoryRepo.findById(operation.getCategoryId())
                        .map(category -> Pair.of(operation, category)))
                .flatMap(pair -> iconRepo.findById(pair.getRight().getIconId())
                        .map(icon -> Triple.of(pair.getLeft(), pair.getRight(), icon)))
                .map(triple -> mapper.toDto(triple.getLeft(), triple.getMiddle(), triple.getRight()));
    }

    @Override
    public Mono<OperationDto> updateOperation(final UUID id, final OperationUpdateDto body) {
        // TODO check user and account
        return repo.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(OperationEntity.class, id.toString()))))
                .map(operation -> {
                    operation.setAmount(body.amount());
                    operation.setDate(body.date());
                    operation.setDescription(body.description());
                    operation.setCategoryId(body.categoryId());
                    return operation;
                })
                .flatMap(repo::save)
                .doOnNext(operation -> log.info("Updated operation: {}", operation.getId()))
                .flatMap(operation -> categoryRepo.findById(operation.getCategoryId())
                        .map(category -> Pair.of(operation, category)))
                .flatMap(pair -> iconRepo.findById(pair.getRight().getIconId())
                        .map(icon -> Triple.of(pair.getLeft(), pair.getRight(), icon)))
                .map(triple -> mapper.toDto(triple.getLeft(), triple.getMiddle(), triple.getRight()));
    }

    @Override
    public Mono<Void> deleteOperation(final UUID id) {
        // TODO check user and account
        return repo.findById(id)
                .switchIfEmpty(Mono.defer(() -> Mono.error(new NotFoundException(OperationEntity.class, id.toString()))))
                .flatMap(repo::delete)
                .doOnSuccess(_ -> log.info("Deleted operation: {}", id));
    }

    @Override
    public Mono<Void> importOperations(final OperationImportDto meta, final FilePart file) {
        // TODO check user and account
        return Mono.fromRunnable(() -> importService.importOperations(meta, file));
    }

    @Override
    public Flux<DataBuffer> exportOperations(final OperationExportDto body, final String filename) {
        // TODO check user and account
        return exportService.exportOperations(body, filename);
    }
}
