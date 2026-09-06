package ru.rgrabelnikov.fbbackend.service;

import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.dto.ListWrapperDto;
import ru.rgrabelnikov.fbbackend.dto.icon.IconCreateDto;
import ru.rgrabelnikov.fbbackend.dto.icon.IconDto;

import java.util.UUID;

public interface IconService {

    Mono<ListWrapperDto<IconDto>> getAllUserIcons();

    Mono<IconDto> createUserIcon(IconCreateDto body);

    Mono<IconDto> updateUserIcon(UUID id, IconCreateDto body);
}
