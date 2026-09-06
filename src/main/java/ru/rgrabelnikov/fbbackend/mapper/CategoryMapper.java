package ru.rgrabelnikov.fbbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.rgrabelnikov.fbbackend.domain.CategoryEntity;
import ru.rgrabelnikov.fbbackend.domain.IconEntity;
import ru.rgrabelnikov.fbbackend.dto.category.CategoryCreateDto;
import ru.rgrabelnikov.fbbackend.dto.category.CategoryDto;
import ru.rgrabelnikov.fbbackend.dto.icon.IconDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;

@Mapper
public interface CategoryMapper {

    @Mapping(target = "icon", expression = "java(toIconDto(icons.get(category.getIconId())))")
    @Mapping(target = "children", expression = "java(toChildrenDtoList(category, icons, subcategories))")
    CategoryDto toDto(CategoryEntity category, Map<UUID, IconEntity> icons, Map<UUID, List<CategoryEntity>> subcategories);

    IconDto toIconDto(IconEntity entity);

    default List<CategoryDto> toChildrenDtoList(CategoryEntity parent, Map<UUID, IconEntity> icons, Map<UUID, List<CategoryEntity>> subcategories) {
        if (isNull(parent) || !subcategories.containsKey(parent.getId())) {
            return emptyList();
        }
        return subcategories.get(parent.getId()).stream()
                .map(child -> toDto(child, icons, subcategories))
                .toList();
    }

    @Mapping(target = "id", source = "category.id")
    @Mapping(target = "icon", source = "icon")
    @Mapping(target = "children", expression = "java(java.util.Collections.emptyList())")
    CategoryDto toDto(CategoryEntity category, IconEntity icon);

    default CategoryEntity toEntity(CategoryCreateDto dto, UUID userId) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId();
        entity.setIncome(dto.income());
        entity.setName(dto.name());
        entity.setUserId(userId);
        entity.setIconId(dto.iconId());
        entity.setParentId(dto.parentId());
        return entity;
    }
}
