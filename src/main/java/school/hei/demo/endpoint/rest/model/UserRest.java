package school.hei.demo.endpoint.rest.model;

import school.hei.demo.model.UserRole;

import java.time.LocalDate;
import java.util.UUID;

public record UserRest (
    UUID id,
    String firstName,
    String lastName,
    LocalDate birthdate,
    String email,
    String phone,
    UserRole role
){}
