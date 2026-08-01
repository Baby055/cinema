package school.hei.demo.endpoint.rest.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import school.hei.demo.model.ReservationStatus;

public record SaveReservation(
    UUID id, @NotNull UUID projectionId, @NotEmpty Set<UUID> seatIds, ReservationStatus status) {}
