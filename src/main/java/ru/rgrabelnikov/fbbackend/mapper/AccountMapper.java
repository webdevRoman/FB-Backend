package ru.rgrabelnikov.fbbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.rgrabelnikov.fbbackend.domain.AccountEntity;
import ru.rgrabelnikov.fbbackend.domain.IconEntity;
import ru.rgrabelnikov.fbbackend.dto.account.AccountCreateDto;
import ru.rgrabelnikov.fbbackend.dto.account.AccountDto;

import java.util.UUID;

@Mapper(uses = {IconMapper.class})
public interface AccountMapper {

    @Mapping(target = "id", source = "account.id")
    @Mapping(target = "icon", source = "icon")
    AccountDto toDto(AccountEntity account, IconEntity icon);

    default AccountEntity toEntity(AccountCreateDto dto, UUID userId) {
        AccountEntity entity = new AccountEntity();
        entity.setId();
        entity.setName(dto.name());
        entity.setIconId(dto.iconId());
        entity.setUserId(userId);
        return entity;
    }
}
