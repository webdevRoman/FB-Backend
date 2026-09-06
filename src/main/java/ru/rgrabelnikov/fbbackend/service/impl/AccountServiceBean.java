package ru.rgrabelnikov.fbbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.domain.AccountEntity;
import ru.rgrabelnikov.fbbackend.domain.IconEntity;
import ru.rgrabelnikov.fbbackend.dto.ListWrapperDto;
import ru.rgrabelnikov.fbbackend.dto.account.AccountCreateDto;
import ru.rgrabelnikov.fbbackend.dto.account.AccountDto;
import ru.rgrabelnikov.fbbackend.exception.NotFoundException;
import ru.rgrabelnikov.fbbackend.mapper.AccountMapper;
import ru.rgrabelnikov.fbbackend.mapper.CommonMapper;
import ru.rgrabelnikov.fbbackend.repo.AccountRepo;
import ru.rgrabelnikov.fbbackend.repo.IconRepo;
import ru.rgrabelnikov.fbbackend.service.AccountService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static ru.rgrabelnikov.fbbackend.util.SecurityUtils.getCurrentUser;

@Log4j2
@Service
@RequiredArgsConstructor
public class AccountServiceBean implements AccountService {

    private final AccountRepo repo;
    private final IconRepo iconRepo;

    private final AccountMapper mapper;
    private final CommonMapper commonMapper;

    @Override
    public Mono<ListWrapperDto<AccountDto>> getAllUserAccounts() {
        final Mono<List<AccountEntity>> accountsMono = getCurrentUser()
                .flatMap(user -> repo.findByUserId(user.getId()).collectList());

        final Mono<Map<UUID, IconEntity>> iconsMono = accountsMono
                .flatMapMany(accounts -> {
                    final List<UUID> iconIds = accounts.stream()
                            .map(AccountEntity::getIconId)
                            .distinct()
                            .toList();

                    return iconRepo.findByIdIn(iconIds);
                })
                .collectMap(IconEntity::getId);

        return Mono.zip(accountsMono, iconsMono)
                .map(tuple -> {
                    final List<AccountEntity> accounts = tuple.getT1();
                    final Map<UUID, IconEntity> iconsMap = tuple.getT2();

                    return accounts.stream()
                            .map(account -> mapper.toDto(account, iconsMap.get(account.getIconId())))
                            .toList();
                })
                .map(commonMapper::toListWrapperDto);
    }

    @Override
    public Mono<AccountDto> createUserAccount(final AccountCreateDto body) {
        return getCurrentUser()
                .map(user -> mapper.toEntity(body, user.getId()))
                .flatMap(repo::save)
                .doOnNext(acc -> log.info("Created account: {}", acc.getId()))
                .flatMap(account -> iconRepo.findById(account.getIconId())
                        .map(icon -> Pair.of(account, icon)))
                .map(pair -> mapper.toDto(pair.getLeft(), pair.getRight()));
    }

    @Override
    public Mono<AccountDto> updateUserAccount(final UUID id, final AccountCreateDto body) {
        return getCurrentUser()
                .flatMapMany(user -> repo.findByIdAndUserId(id, user.getId())
                        .switchIfEmpty(Mono.defer(() ->
                                Mono.error(new NotFoundException(AccountEntity.class, format("id = %s, userId = %s", id, user.getId()))))))
                .next()
                .map(acc -> {
                    acc.setName(body.name());
                    acc.setIconId(body.iconId());
                    return acc;
                })
                .flatMap(repo::save)
                .doOnNext(acc -> log.info("Updated account: {}", acc.getId()))
                .flatMap(account -> iconRepo.findById(account.getIconId())
                        .map(icon -> Pair.of(account, icon)))
                .map(pair -> mapper.toDto(pair.getLeft(), pair.getRight()));
    }
}
