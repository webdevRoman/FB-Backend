package ru.rgrabelnikov.fbbackend.mapper;

import org.mapstruct.Mapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.rgrabelnikov.fbbackend.dto.ContextUser;
import ru.rgrabelnikov.fbbackend.model.UserEntity;

import java.util.Set;

@Mapper
public interface UserMapper {

    default ContextUser toContextUser(UserEntity userEntity) {
        final Set<SimpleGrantedAuthority> authority = Set.of(
                toGrantedAuthority(userEntity.getRole().name())
        );
        return new ContextUser(userEntity.getLogin(), userEntity.getPassword(),
                true, true, true, true,
                authority, userEntity.getId());
    }

    default SimpleGrantedAuthority toGrantedAuthority(String role) {
        return new SimpleGrantedAuthority("ROLE_" + role);
    }
}