package ru.rgrabelnikov.fbbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.rgrabelnikov.fbbackend.domain.CategoryEntity;
import ru.rgrabelnikov.fbbackend.domain.IconEntity;
import ru.rgrabelnikov.fbbackend.domain.OperationEntity;
import ru.rgrabelnikov.fbbackend.dto.category.CategoryViewDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationCreateDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationExportFileModelDto;
import ru.rgrabelnikov.fbbackend.dto.operation.OperationImportFileModelDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Mapper(uses = IconMapper.class)
public interface OperationMapper {

    @Mapping(target = "category", expression = "java(toCategoryViewDto(operation, categories, icons))")
    OperationDto toDto(OperationEntity operation, Map<UUID, CategoryEntity> categories, Map<UUID, IconEntity> icons);

    default CategoryViewDto toCategoryViewDto(OperationEntity operation, Map<UUID, CategoryEntity> categories, Map<UUID, IconEntity> icons) {
        if (isNull(operation.getCategoryId()) || !categories.containsKey(operation.getCategoryId())) {
            return null;
        }

        final CategoryEntity category = categories.get(operation.getCategoryId());
        final IconEntity icon = isNull(category.getIconId()) ? null : icons.get(category.getIconId());
        return toCategoryViewDto(category, icon);
    }

    @Mapping(target = "id", source = "category.id")
    @Mapping(target = "icon", source = "icon")
    CategoryViewDto toCategoryViewDto(CategoryEntity category, IconEntity icon);

    @Mapping(target = "id", source = "operation.id")
    @Mapping(target = "category", expression = "java(toCategoryViewDto(category, icon))")
    OperationDto toDto(OperationEntity operation, CategoryEntity category, IconEntity icon);

    default OperationEntity toEntity(OperationCreateDto dto) {
        final OperationEntity entity = new OperationEntity();
        entity.setId();
        entity.setAccountId(dto.accountId());
        entity.setCategoryId(dto.categoryId());
        entity.setAmount(dto.amount());
        entity.setDate(dto.date());
        entity.setDescription(dto.description());
        return entity;
    }

    default OperationEntity toEntity(OperationImportFileModelDto dto, UUID categoryId, DateTimeFormatter dateFormatter, UUID accountId) {
        final BigDecimal amount = new BigDecimal(dto.getAmount().replaceAll("[\\s ]", ""));
        final LocalDate date = isBlank(dto.getDate())
                ? LocalDate.of(2020, 1, 1)
                : LocalDate.parse(dto.getDate(), dateFormatter);
        final OperationEntity entity = new OperationEntity();
        entity.setId();
        entity.setAccountId(accountId);
        entity.setCategoryId(categoryId);
        entity.setAmount(amount.abs());
        entity.setDate(date);
        entity.setDescription(dto.getDescription());
        return entity;
    }

    @Mapping(target = "date", source = "operation.date")
    @Mapping(target = "amount", source = "operation.amount")
    @Mapping(target = "description", source = "operation.description")
    @Mapping(target = "income", source = "category.income")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    OperationExportFileModelDto toExportModel(OperationEntity operation, CategoryEntity category);
}
