package ru.rgrabelnikov.fbbackend.service;

import reactor.core.publisher.Mono;
import ru.rgrabelnikov.fbbackend.dto.ListWrapperDto;
import ru.rgrabelnikov.fbbackend.dto.category.CategoryCreateDto;
import ru.rgrabelnikov.fbbackend.dto.category.CategoryDto;
import ru.rgrabelnikov.fbbackend.dto.category.CategoryUpdateDto;

import java.util.UUID;

public interface CategoryService {

    Mono<ListWrapperDto<CategoryDto>> getAllUserCategories(Boolean income);

    Mono<CategoryDto> createUserCategory(CategoryCreateDto body);

    Mono<CategoryDto> updateUserCategory(UUID id, CategoryUpdateDto body);
}
