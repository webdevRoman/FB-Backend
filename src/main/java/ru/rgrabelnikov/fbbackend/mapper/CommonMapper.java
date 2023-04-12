package ru.rgrabelnikov.fbbackend.mapper;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mapstruct.Mapper;
import org.springframework.http.ResponseEntity;
import ru.rgrabelnikov.fbbackend.dto.IdNameDto;
import ru.rgrabelnikov.fbbackend.dto.ServerErrorDto;
import ru.rgrabelnikov.fbbackend.dto.ValidationErrorDto;

import java.util.List;

@Mapper
public interface CommonMapper {

    default ResponseEntity<List<ValidationErrorDto>> toValidationErrorResponse(List<ValidationErrorDto> errors) {
        return ResponseEntity.badRequest().body(errors);
    }

    default ResponseEntity<List<ValidationErrorDto>> toValidationErrorResponse(String field, String error) {
        return ResponseEntity.badRequest().body(
                List.of(toValidationErrorDto(field, error))
        );
    }

    default ResponseEntity<ServerErrorDto> toServerErrorResponse(Exception ex) {
        return ResponseEntity.internalServerError().body(toServerErrorDto(ex));
    }

    ValidationErrorDto toValidationErrorDto(String field, String error);

    default ServerErrorDto toServerErrorDto(Exception ex) {
        ServerErrorDto dto = new ServerErrorDto();
        dto.setMessage(ex.getMessage());
        dto.setStacktrace(ExceptionUtils.getStackTrace(ex));
        return dto;
    }

    IdNameDto toIdNameDto(String id, String name);
}