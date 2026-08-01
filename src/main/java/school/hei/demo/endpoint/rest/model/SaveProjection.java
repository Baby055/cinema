package school.hei.demo.endpoint.rest.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record SaveProjection(
    UUID id, Instant datetime, BigDecimal seatPrice, UUID movieId, UUID roomId) {}
