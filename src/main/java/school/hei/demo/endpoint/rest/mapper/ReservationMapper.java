package school.hei.demo.endpoint.rest.mapper;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import school.hei.demo.endpoint.rest.model.ReservationRest;
import school.hei.demo.model.Reservation;
import school.hei.demo.model.Seat;

import java.util.stream.Collectors;

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