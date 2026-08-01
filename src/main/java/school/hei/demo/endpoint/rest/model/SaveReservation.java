package school.hei.demo.endpoint.rest.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import school.hei.demo.model.ReservationStatus;

import java.util.Set;
import java.util.UUID;

public record SaveReservation(
        UUID id, @NotNull UUID projectionId, @NotEmpty Set<UUID> seatIds, ReservationStatus status) {}
