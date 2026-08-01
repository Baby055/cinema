package school.hei.demo.endpoint.rest.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProjectionRest(
    UUID id, Instant Datetime, BigDecimal seatPrice, UUID movieId, UUID roomid) {}
