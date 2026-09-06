package ru.rgrabelnikov.fbbackend.mapper;

import org.mapstruct.Mapper;
import ru.rgrabelnikov.fbbackend.domain.IconEntity;
import ru.rgrabelnikov.fbbackend.dto.icon.IconCreateDto;
import ru.rgrabelnikov.fbbackend.dto.icon.IconDto;

import java.util.UUID;

@Mapper
public interface IconMapper {

    IconDto toDto(IconEntity entity);

    default IconEntity toEntity(IconCreateDto dto, UUID userId) {
        IconEntity entity = new IconEntity();
        entity.setId();
        entity.setUserId(userId);
        entity.setIcon(dto.icon());
        entity.setTooltip(dto.tooltip());
        return entity;
    }
}
