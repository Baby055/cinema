package school.hei.demo.endpoint.rest.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.Set;
import java.util.UUID;
import school.hei.demo.model.Genre;

public record SaveMovie(
    UUID id,
    @NotBlank String title,
    @NotEmpty Set<Genre> genres,
    @NotBlank String description,
    @Positive long durationInMinutes) {}
