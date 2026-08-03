package school.hei.demo.endpoint.rest.controller;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import school.hei.demo.endpoint.rest.mapper.ReservationMapper;
import school.hei.demo.endpoint.rest.model.ReservationRest;
import school.hei.demo.endpoint.rest.model.SaveReservation;
import school.hei.demo.service.ReservationService;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    @PreAuthorize("hasAnyRole('EMPLOYEE' , 'MANAGER')")
    @GetMapping("/reservations")
    public List<ReservationRest> findAll() {
        return reservationService.findAll().stream().map(reservationMapper::toRest).toList();
    }

    @GetMapping("/reservations/{id}")
    public ReservationRest findById(@PathVariable UUID id) {
        return reservationMapper.toRest(reservationService.findById(id));
    }

    @PutMapping("/reservation")
    public ReservationRest save(@RequestBody SaveReservation toSave) {
        return reservationMapper.toRest(reservationService.save(toSave));
    }
}
