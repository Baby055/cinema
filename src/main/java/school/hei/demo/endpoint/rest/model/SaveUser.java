package school.hei.demo.endpoint.rest.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import school.hei.demo.model.UserRole;

import java.time.LocalDate;
import java.util.UUID;

public record SaveUser (
        UUID id,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull LocalDate birthdate,
        @NotBlank @Email String email,
        String password,
        @NotBlank String phone,
        UserRole role
){}
