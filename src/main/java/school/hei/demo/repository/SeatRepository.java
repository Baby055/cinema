package school.hei.demo.repository;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.demo.model.Seat;
import school.hei.demo.repository.model.JSeat;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@AllArgsConstructor
public class SeatRepository {
    private JSeatRepository jSeatRepository;
    private JRoomRepository jRoomRepository;

    public List<Seat> findByRoomId(UUID roomId){
        return jSeatRepository.findByRoomId(roomId).stream()
                .map(SeatRepository::toDomain)
                .toList();
    }

    public List<Seat> findByIdIn(List<UUID> ids){
        return jSeatRepository.findByRoomId(ids).stream()
                .map(SeatRepository::toDomain)
                .toList();
    }

    public Optional<Seat> findById(UUID id){
        return jSeatRepository.findById(id).map(SeatRepository::toDomain);
    }

    public Seat save(Seat seat){
        jRoom jRoom =
                jRoomRepository
                        .findById(seat.getRoom().getId())
                        .orElseThrow(
                                () -> new IllegalStateException("Room "+ seat.getRoom().getId() + "not found")
                        );
        JSeat toSave =
                new JSeat(seat.getId(), seat.getNumber(), jRoom);
        return toDomain(jSeatRepository.save(toSave));
    }

    public static Seat toDomain(JSeat entity){
        return new Seat(
                entity.getId(),
                entity.getNumber(),
                RoomRepository.toDomain(entity.getRoom())
        );
    }
}
