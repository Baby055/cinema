package school.hei.demo.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.demo.model.Reservation;
import school.hei.demo.model.Seat;
import school.hei.demo.repository.model.JProjection;
import school.hei.demo.repository.model.JReservation;
import school.hei.demo.repository.model.JSeat;
import school.hei.demo.repository.model.JUser;

@Repository
@AllArgsConstructor
public class ReservationRepository {
  private final JReservationRepository jReservationRepository;
  private final JProjectionRepository jProjectionRepository;
  private final JUserRepository jUserRepository;
  private final JSeatRepository jSeatRepository;

  public List<Reservation> findAll() {
    return jReservationRepository.findAll().stream().map(ReservationRepository::toDomain).toList();
  }

  public Optional<Reservation> findById(UUID id) {
    return jReservationRepository.findById(id).map(ReservationRepository::toDomain);
  }

  public List<Reservation> findByProjectionId(UUID projectionId) {
    return jReservationRepository.findByProjectionId(projectionId).stream()
        .map(ReservationRepository::toDomain)
        .toList();
  }

  public List<Reservation> findByUserId(UUID userId) {
    return jReservationRepository.findByUserId(userId).stream()
        .map(ReservationRepository::toDomain)
        .toList();
  }

  public Reservation save(Reservation reservation) {
    JProjection jProjection =
        jProjectionRepository
            .findById(reservation.getProjection().getId())
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Projecition " + reservation.getProjection().getId() + " not found"));
    JUser jUser =
        jUserRepository
            .findById(reservation.getUser().getId())
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "User " + reservation.getUser().getId() + " not found"));
    Set<JSeat> jSeats =
        Set.copyOf(
            jSeatRepository.findByIdIn(
                reservation.getSeats().stream().map(Seat::getId).collect(Collectors.toList())));
    JReservation toSave =
        new JReservation(
            reservation.getId(),
            reservation.getCreatedAt(),
            reservation.getStatus(),
            jProjection,
            jUser,
            jSeats);
    return toDomain(jReservationRepository.save(toSave));
  }

  public static Reservation toDomain(JReservation entity) {
    return new Reservation(
        entity.getId(),
        entity.getCreatedAt(),
        entity.getStatus(),
        ProjectionRepository.toDomain(entity.getProjection()),
        UserRepository.toDomain(entity.getUser()),
        entity.getSeats().stream().map(SeatRepository::toDomain).collect(Collectors.toSet()));
  }
}
