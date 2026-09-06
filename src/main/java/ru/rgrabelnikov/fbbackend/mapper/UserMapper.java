package ru.rgrabelnikov.fbbackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.rgrabelnikov.fbbackend.domain.UserEntity;
import ru.rgrabelnikov.fbbackend.dto.security.ContextUser;
import ru.rgrabelnikov.fbbackend.dto.security.Role;
import ru.rgrabelnikov.fbbackend.dto.user.JwtDto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Mapper
public interface UserMapper {

    JwtDto toJwtDto(String jwt);

    @Mapping(target = "username", source = "login")
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "accountNonExpired", constant = "true")
    @Mapping(target = "credentialsNonExpired", constant = "true")
    @Mapping(target = "accountNonLocked", constant = "true")
    @Mapping(target = "authorities", source = "role", qualifiedByName = "toGrantedAuthorities")
    ContextUser toContextUser(UserEntity userEntity);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "accountNonExpired", constant = "true")
    @Mapping(target = "credentialsNonExpired", constant = "true")
    @Mapping(target = "accountNonLocked", constant = "true")
    ContextUser toContextUser(UUID id, String username, List<SimpleGrantedAuthority> authorities);

    @Named("toGrantedAuthorities")
    default Set<SimpleGrantedAuthority> toGrantedAuthorities(Role role) {
        return Set.of(toGrantedAuthority(role.name()));
    }

    default SimpleGrantedAuthority toGrantedAuthority(String role) {
        return new SimpleGrantedAuthority("ROLE_" + role);
    }
}
