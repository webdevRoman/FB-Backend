package ru.rgrabelnikov.fbbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.domain.IconEntity;
import ru.rgrabelnikov.fbbackend.dto.ListWrapperDto;
import ru.rgrabelnikov.fbbackend.dto.icon.IconCreateDto;
import ru.rgrabelnikov.fbbackend.dto.icon.IconDto;
import ru.rgrabelnikov.fbbackend.exception.NotFoundException;
import ru.rgrabelnikov.fbbackend.mapper.CommonMapper;
import ru.rgrabelnikov.fbbackend.mapper.IconMapper;
import ru.rgrabelnikov.fbbackend.repo.IconRepo;
import ru.rgrabelnikov.fbbackend.service.IconService;

import java.util.UUID;

import static java.lang.String.format;
import static ru.rgrabelnikov.fbbackend.util.SecurityUtils.getCurrentUser;

@Log4j2
@Service
@RequiredArgsConstructor
public class IconServiceBean implements IconService {

    private final IconRepo repo;

    private final IconMapper mapper;
    private final CommonMapper commonMapper;

    @Override
    public Mono<ListWrapperDto<IconDto>> getAllUserIcons() {
        return getCurrentUser()
                .flatMapMany(user -> repo.findByUserId(user.getId()))
                .map(mapper::toDto)
                .collectList()
                .map(commonMapper::toListWrapperDto);
    }

    @Override
    @Transactional
    public Mono<IconDto> createUserIcon(IconCreateDto body) {
        return getCurrentUser()
                .map(user -> mapper.toEntity(body, user.getId()))
                .flatMap(repo::save)
                .doOnNext(icon -> log.info("Created icon: {}", icon.getId()))
                .map(mapper::toDto);
    }

    @Override
    @Transactional
    public Mono<IconDto> updateUserIcon(UUID id, IconCreateDto body) {
        return getCurrentUser()
                .flatMapMany(user -> repo.findByIdAndUserId(id, user.getId())
                        .switchIfEmpty(Mono.defer(() ->
                                Mono.error(new NotFoundException(IconEntity.class, format("id = %s, userId = %s", id, user.getId()))))))
                .next()
                .map(icon -> {
                    icon.setIcon(body.icon());
                    icon.setTooltip(body.tooltip());
                    return icon;
                })
                .flatMap(repo::save)
                .doOnNext(icon -> log.info("Updated icon: {}", icon.getId()))
                .map(mapper::toDto);
    }
}
