package school.hei.demo.endpoint.rest.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import school.hei.demo.model.UserRole;

public record SaveUser(
    UUID id,
    @NotBlank String firstName,
    @NotBlank String lastName,
    @NotNull LocalDate birthdate,
    @NotBlank @Email String email,
    String password,
    @NotBlank String phone,
    UserRole role) {}
