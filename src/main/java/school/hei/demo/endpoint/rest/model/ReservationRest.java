package school.hei.demo.endpoint.rest.model;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import school.hei.demo.model.ReservationStatus;

public record ReservationRest(
        UUID id,
        Instant createdAt,
        ReservationStatus status,
        UUID projectionId,
        UUID userId,
        Set<UUID> seatIds) {}

