package school.hei.demo.service;

import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.demo.endpoint.rest.exception.BadRequestException;
import school.hei.demo.endpoint.rest.exception.ForbiddenException;
import school.hei.demo.endpoint.rest.exception.NotFoundException;
import school.hei.demo.endpoint.rest.model.SaveReservation;
import school.hei.demo.model.*;
import school.hei.demo.repository.ProjectionRepository;
import school.hei.demo.repository.ReservationRepository;
import school.hei.demo.repository.SeatRepository;
import school.hei.demo.repository.UserRepository;

@Service
@AllArgsConstructor
public class ReservationService {

  private final ReservationRepository reservationRepository;
  private final ProjectionRepository projectionRepository;
  private final SeatRepository seatRepository;
  private final UserRepository userRepository;
  private final CurrentUserProvider currentUserProvider;

  public List<Reservation> findAll() {
    return reservationRepository.findAll();
  }

  public Reservation findById(UUID id) {
    Reservation reservation = getExistingOrThrow(id);
    AuthenticatedUser currentUser = currentUserProvider.get();
    if (isOwnReservationRequiredButNotOwned(currentUser, reservation)) {
      throw new ForbiddenException("You cannot access another client's reservation");
    }
    return reservation;
  }

  @Transactional
  public Reservation save(SaveReservation toSave) {
    AuthenticatedUser currentUser = currentUserProvider.get();
    boolean isCreation = toSave.id() == null;

    Projection projection =
        projectionRepository
            .findById(toSave.projectionId())
            .orElseThrow(
                () -> new NotFoundException("Projection " + toSave.projectionId() + " not found"));
    Set<Seat> seats = resolveAndValidateSeats(toSave, projection);

    Reservation reservation;
    if (isCreation) {
      reservation = createReservation(currentUser, projection, seats);
    } else {
      reservation = getExistingOrThrow(toSave.id());
      if (isOwnReservationRequiredButNotOwned(currentUser, reservation)) {
        throw new ForbiddenException("You cannot update another client's reservation");
      }
      reservation.setProjection(projection);
      reservation.setSeats(seats);
      applyStatusChange(currentUser, reservation, toSave.status());
    }
    return reservationRepository.save(reservation);
  }

  private Reservation createReservation(
      AuthenticatedUser currentUser, Projection projection, Set<Seat> seats) {
    User owner =
        userRepository
            .findById(currentUser.getId())
            .orElseThrow(() -> new NotFoundException("User " + currentUser.getId() + " not found"));
    Reservation reservation = new Reservation();
    reservation.setId(UUID.randomUUID());
    reservation.setCreatedAt(Instant.now());
    reservation.setStatus(ReservationStatus.PENDING);
    reservation.setProjection(projection);
    reservation.setUser(owner);
    reservation.setSeats(seats);
    return reservation;
  }

  private void applyStatusChange(
      AuthenticatedUser currentUser, Reservation reservation, ReservationStatus requestedStatus) {
    if (requestedStatus == null || requestedStatus == reservation.getStatus()) {
      return;
    }
    if (currentUser.getRole() == UserRole.CLIENT) {
      throw new ForbiddenException("Only an employee or a manager can validate a reservation");
    }
    reservation.setStatus(requestedStatus);
  }

  private Set<Seat> resolveAndValidateSeats(SaveReservation toSave, Projection projection) {
    List<Seat> foundSeats = seatRepository.findByIdIn(List.copyOf(toSave.seatIds()));
    if (foundSeats.size() != toSave.seatIds().size()) {
      throw new NotFoundException("One or more seats could not be found");
    }
    for (Seat seat : foundSeats) {
      if (!seat.getRoom().getId().equals(projection.getRoom().getId())) {
        throw new BadRequestException(
            "Seat " + seat.getId() + " does not belong to the projection's room");
      }
    }
    Set<Seat> seats = new HashSet<>(foundSeats);
    boolean seatAlreadyTaken =
        projectionRepository.findById(projection.getId()).stream()
            .flatMap(p -> reservationRepository.findByProjectionId(p.getId()).stream())
            .filter(existing -> toSave.id() == null || !existing.getId().equals(toSave.id()))
            .filter(existing -> existing.getStatus() != ReservationStatus.CANCELED)
            .anyMatch(existing -> !java.util.Collections.disjoint(existing.getSeats(), seats));
    if (seatAlreadyTaken) {
      throw new BadRequestException("One or more seats are already booked for this projection");
    }
    return seats;
  }

  private Reservation getExistingOrThrow(UUID id) {
    return reservationRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Reservation " + id + " not found"));
  }

  private boolean isOwnReservationRequiredButNotOwned(
      AuthenticatedUser currentUser, Reservation reservation) {
    return currentUser.getRole() == UserRole.CLIENT
        && !reservation.getUser().getId().equals(currentUser.getId());
  }
}
