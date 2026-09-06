package ru.rgrabelnikov.fbbackend.service;

import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.dto.ListWrapperDto;
import ru.rgrabelnikov.fbbackend.dto.account.AccountCreateDto;
import ru.rgrabelnikov.fbbackend.dto.account.AccountDto;

import java.util.UUID;

public interface AccountService {

    Mono<ListWrapperDto<AccountDto>> getAllUserAccounts();

    Mono<AccountDto> createUserAccount(AccountCreateDto body);

    Mono<AccountDto> updateUserAccount(UUID id, AccountCreateDto body);
}
