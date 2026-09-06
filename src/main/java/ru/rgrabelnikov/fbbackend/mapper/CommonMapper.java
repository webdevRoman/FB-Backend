package ru.rgrabelnikov.fbbackend.mapper;

import org.mapstruct.Mapper;
import ru.rgrabelnikov.fbbackend.dto.IdNameDto;
import ru.rgrabelnikov.fbbackend.dto.ListWrapperDto;

import java.util.List;
import java.util.UUID;

@Mapper
public interface CommonMapper {

    IdNameDto toIdNameDto(UUID id, String name);

    default <T> ListWrapperDto<T> toListWrapperDto(List<T> items) {
        return new ListWrapperDto<>(items);
    }
}
