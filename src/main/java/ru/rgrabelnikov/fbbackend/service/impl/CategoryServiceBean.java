package ru.rgrabelnikov.fbbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.domain.CategoryEntity;
import ru.rgrabelnikov.fbbackend.domain.IconEntity;
import ru.rgrabelnikov.fbbackend.dto.ListWrapperDto;
import ru.rgrabelnikov.fbbackend.dto.category.CategoryCreateDto;
import ru.rgrabelnikov.fbbackend.dto.category.CategoryDto;
import ru.rgrabelnikov.fbbackend.dto.category.CategoryUpdateDto;
import ru.rgrabelnikov.fbbackend.exception.NotFoundException;
import ru.rgrabelnikov.fbbackend.mapper.CategoryMapper;
import ru.rgrabelnikov.fbbackend.mapper.CommonMapper;
import ru.rgrabelnikov.fbbackend.repo.CategoryRepo;
import ru.rgrabelnikov.fbbackend.repo.IconRepo;
import ru.rgrabelnikov.fbbackend.service.CategoryService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.groupingBy;
import static ru.rgrabelnikov.fbbackend.util.SecurityUtils.getCurrentUser;

@Log4j2
@Service
@RequiredArgsConstructor
public class CategoryServiceBean implements CategoryService {

    private final CategoryRepo repo;
    private final IconRepo iconRepo;

    private final CategoryMapper mapper;
    private final CommonMapper commonMapper;

    @Override
    public Mono<ListWrapperDto<CategoryDto>> getAllUserCategories(final Boolean income) {
        final Mono<List<CategoryEntity>> categoriesMono = getCurrentUser()
                .flatMapMany(user -> repo.findByUserIdAndIncome(user.getId(), income))
                .collectList();

        final Mono<Map<UUID, IconEntity>> iconsMono = categoriesMono
                .flatMapMany(categories -> {
                    final List<UUID> iconIds = categories.stream()
                            .map(CategoryEntity::getIconId)
                            .distinct()
                            .toList();

                    return iconRepo.findByIdIn(iconIds);
                })
                .collectMap(IconEntity::getId);

        return Mono.zip(categoriesMono, iconsMono)
                .map(tuple -> {
                    final List<CategoryEntity> categories = tuple.getT1();
                    final Map<UUID, IconEntity> iconsMap = tuple.getT2();

                    final Map<UUID, List<CategoryEntity>> subcategories = categories.stream()
                            .filter(category -> nonNull(category.getParentId()))
                            .collect(groupingBy(CategoryEntity::getParentId));

                    return categories.stream()
                            .filter(category -> isNull(category.getParentId()))
                            .map(category -> mapper.toDto(category, iconsMap, subcategories))
                            .toList();
                })
                .map(commonMapper::toListWrapperDto);
    }

    @Override
    public Mono<CategoryDto> createUserCategory(final CategoryCreateDto body) {
        return getCurrentUser()
                // TODO check if parent category matches child by "income" field
                .map(user -> mapper.toEntity(body, user.getId()))
                .flatMap(repo::save)
                .doOnNext(category -> log.info("Created category: {}", category.getId()))
                .flatMap(category -> iconRepo.findById(category.getIconId())
                        .map(icon -> Pair.of(category, icon)))
                .map(pair -> mapper.toDto(pair.getLeft(), pair.getRight()));
    }

    @Override
    public Mono<CategoryDto> updateUserCategory(final UUID id, final CategoryUpdateDto body) {
        return getCurrentUser()
                .flatMapMany(user -> repo.findByIdAndUserId(id, user.getId())
                        .switchIfEmpty(Mono.defer(() ->
                                Mono.error(new NotFoundException(CategoryEntity.class, format("id = %s, userId = %s", id, user.getId()))))))
                .next()
                .map(category -> {
                    category.setName(body.name());
                    category.setIconId(body.iconId());
                    category.setParentId(body.parentId());
                    return category;
                })
                .flatMap(repo::save)
                .doOnNext(category -> log.info("Updated category: {}", category.getId()))
                .flatMap(category -> iconRepo.findById(category.getIconId())
                        .map(icon -> Pair.of(category, icon)))
                .map(pair -> mapper.toDto(pair.getLeft(), pair.getRight()));
    }
}
