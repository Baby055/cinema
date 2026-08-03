package school.hei.demo.endpoint.rest.mapper;

import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import school.hei.demo.endpoint.rest.model.ReservationRest;
import school.hei.demo.model.Reservation;
import school.hei.demo.model.Seat;

@Component
public class ReservationMapper {

  public ReservationRest toRest(Reservation reservation) {
    return new ReservationRest(
        reservation.getId(),
        reservation.getCreatedAt(),
        reservation.getStatus(),
        reservation.getProjection().getId(),
        reservation.getUser().getId(),
        reservation.getSeats().stream().map(Seat::getId).collect(Collectors.toSet()));
  }
}
