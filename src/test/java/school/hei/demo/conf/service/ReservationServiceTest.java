package school.hei.demo.conf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.demo.endpoint.rest.exception.BadRequestException;
import school.hei.demo.endpoint.rest.exception.ForbiddenException;
import school.hei.demo.endpoint.rest.model.SaveReservation;
import school.hei.demo.endpoint.rest.security.AuthenticatedUser;
import school.hei.demo.endpoint.rest.security.CurrentUserProvider;
import school.hei.demo.model.Projection;
import school.hei.demo.model.Reservation;
import school.hei.demo.model.ReservationStatus;
import school.hei.demo.model.Room;
import school.hei.demo.model.Seat;
import school.hei.demo.model.User;
import school.hei.demo.model.UserRole;
import school.hei.demo.repository.ProjectionRepository;
import school.hei.demo.repository.ReservationRepository;
import school.hei.demo.repository.SeatRepository;
import school.hei.demo.repository.UserRepository;
import school.hei.demo.service.ReservationService;

class ReservationServiceTest {

  private ReservationRepository reservationRepository;
  private ProjectionRepository projectionRepository;
  private SeatRepository seatRepository;
  private UserRepository userRepository;
  private CurrentUserProvider currentUserProvider;
  private ReservationService subject;

  private Room room;
  private Projection projection;
  private Seat seat;
  private User clientUser;

  @BeforeEach
  void setUp() {
    reservationRepository = mock(ReservationRepository.class);
    projectionRepository = mock(ProjectionRepository.class);
    seatRepository = mock(SeatRepository.class);
    userRepository = mock(UserRepository.class);
    currentUserProvider = mock(CurrentUserProvider.class);
    subject =
        new ReservationService(
            reservationRepository,
            projectionRepository,
            seatRepository,
            userRepository,
            currentUserProvider);

    room = new Room(UUID.randomUUID(), "1", 50);
    seat = new Seat(UUID.randomUUID(), "A1", room);
    projection = new Projection(UUID.randomUUID(), Instant.now(), BigDecimal.TEN, null, room);
    clientUser =
        new User(
            UUID.randomUUID(),
            "Rindra",
            "Rakoto",
            java.time.LocalDate.of(2000, 1, 1),
            "rindra@hei.school",
            "hashed",
            "0340000000",
            UserRole.CLIENT);
  }

  private AuthenticatedUser asAuthenticatedUser(User user) {
    return new AuthenticatedUser(user);
  }

  @Test
  void client_can_read_own_reservation() {
    Reservation reservation = reservationOf(clientUser, ReservationStatus.PENDING);
    when(reservationRepository.findById(reservation.getId()))
        .thenReturn(java.util.Optional.of(reservation));
    when(currentUserProvider.get()).thenReturn(asAuthenticatedUser(clientUser));

    Reservation found = subject.findById(reservation.getId());

    assertThat(found).isEqualTo(reservation);
  }

  @Test
  void client_cannot_read_another_clients_reservation() {
    User otherClient = cloneWithNewId(clientUser);
    Reservation reservation = reservationOf(otherClient, ReservationStatus.PENDING);
    when(reservationRepository.findById(reservation.getId()))
        .thenReturn(java.util.Optional.of(reservation));
    when(currentUserProvider.get()).thenReturn(asAuthenticatedUser(clientUser));

    assertThatThrownBy(() -> subject.findById(reservation.getId()))
        .isInstanceOf(ForbiddenException.class);
  }

  @Test
  void employee_can_read_any_reservation() {
    User otherClient = cloneWithNewId(clientUser);
    Reservation reservation = reservationOf(otherClient, ReservationStatus.PENDING);
    when(reservationRepository.findById(reservation.getId()))
        .thenReturn(java.util.Optional.of(reservation));
    User employee = cloneWithRole(clientUser, UserRole.EMPLOYEE);
    when(currentUserProvider.get()).thenReturn(asAuthenticatedUser(employee));

    Reservation found = subject.findById(reservation.getId());

    assertThat(found).isEqualTo(reservation);
  }

  @Test
  void creating_a_reservation_always_starts_as_pending_even_if_client_requests_success() {
    when(currentUserProvider.get()).thenReturn(asAuthenticatedUser(clientUser));
    when(userRepository.findById(clientUser.getId())).thenReturn(java.util.Optional.of(clientUser));
    when(projectionRepository.findById(projection.getId()))
        .thenReturn(java.util.Optional.of(projection));
    when(seatRepository.findByIdIn(any())).thenReturn(List.of(seat));
    when(reservationRepository.findByProjectionId(projection.getId())).thenReturn(List.of());
    when(reservationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    SaveReservation toSave =
        new SaveReservation(
            null, projection.getId(), Set.of(seat.getId()), ReservationStatus.SUCCESS);

    Reservation saved = subject.save(toSave);

    assertThat(saved.getStatus()).isEqualTo(ReservationStatus.PENDING);
  }

  @Test
  void client_cannot_validate_an_existing_reservation() {
    Reservation reservation = reservationOf(clientUser, ReservationStatus.PENDING);
    when(currentUserProvider.get()).thenReturn(asAuthenticatedUser(clientUser));
    when(reservationRepository.findById(reservation.getId()))
        .thenReturn(java.util.Optional.of(reservation));
    when(projectionRepository.findById(projection.getId()))
        .thenReturn(java.util.Optional.of(projection));
    when(seatRepository.findByIdIn(any())).thenReturn(List.of(seat));
    when(reservationRepository.findByProjectionId(projection.getId())).thenReturn(List.of());

    SaveReservation toSave =
        new SaveReservation(
            reservation.getId(),
            projection.getId(),
            Set.of(seat.getId()),
            ReservationStatus.SUCCESS);

    assertThatThrownBy(() -> subject.save(toSave)).isInstanceOf(ForbiddenException.class);
  }

  @Test
  void employee_can_validate_an_existing_reservation() {
    Reservation reservation = reservationOf(clientUser, ReservationStatus.PENDING);
    User employee = cloneWithRole(clientUser, UserRole.EMPLOYEE);
    when(currentUserProvider.get()).thenReturn(asAuthenticatedUser(employee));
    when(reservationRepository.findById(reservation.getId()))
        .thenReturn(java.util.Optional.of(reservation));
    when(projectionRepository.findById(projection.getId()))
        .thenReturn(java.util.Optional.of(projection));
    when(seatRepository.findByIdIn(any())).thenReturn(List.of(seat));
    when(reservationRepository.findByProjectionId(projection.getId()))
        .thenReturn(List.of(reservation));
    when(reservationRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    SaveReservation toSave =
        new SaveReservation(
            reservation.getId(),
            projection.getId(),
            Set.of(seat.getId()),
            ReservationStatus.SUCCESS);

    Reservation saved = subject.save(toSave);

    assertThat(saved.getStatus()).isEqualTo(ReservationStatus.SUCCESS);
  }

  @Test
  void booking_an_already_taken_seat_is_rejected() {
    Reservation existing = reservationOf(clientUser, ReservationStatus.SUCCESS);
    when(currentUserProvider.get()).thenReturn(asAuthenticatedUser(clientUser));
    when(userRepository.findById(clientUser.getId())).thenReturn(java.util.Optional.of(clientUser));
    when(projectionRepository.findById(projection.getId()))
        .thenReturn(java.util.Optional.of(projection));
    when(seatRepository.findByIdIn(any())).thenReturn(List.of(seat));
    when(reservationRepository.findByProjectionId(projection.getId()))
        .thenReturn(List.of(existing));

    SaveReservation toSave =
        new SaveReservation(null, projection.getId(), Set.of(seat.getId()), null);

    assertThatThrownBy(() -> subject.save(toSave)).isInstanceOf(BadRequestException.class);
  }

  private Reservation reservationOf(User owner, ReservationStatus status) {
    return new Reservation(
        UUID.randomUUID(), Instant.now(), status, projection, owner, Set.of(seat));
  }

  private User cloneWithNewId(User base) {
    return new User(
        UUID.randomUUID(),
        base.getFirstName(),
        base.getLastName(),
        base.getBirthDate(),
        "other-" + base.getEmail(),
        base.getPassword(),
        base.getPhone(),
        base.getRole());
  }

  private User cloneWithRole(User base, UserRole role) {
    return new User(
        UUID.randomUUID(),
        base.getFirstName(),
        base.getLastName(),
        base.getBirthDate(),
        "staff-" + base.getEmail(),
        base.getPassword(),
        base.getPhone(),
        role);
  }
}
