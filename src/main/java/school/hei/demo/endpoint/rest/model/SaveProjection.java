package school.hei.demo.endpoint.rest.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SaveProjection(
        UUID id, @NotNull Instant datetime, @NotNull @Positive BigDecimal seatPrice, @NotNull UUID movieId, @NotNull UUID roomId) {}
