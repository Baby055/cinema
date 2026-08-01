package school.hei.demo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.demo.model.Room;
import school.hei.demo.repository.model.JRoom;

@Repository
@AllArgsConstructor
public class RoomRepository {
  private final JRoomRepository jRoomRepository;

  public List<Room> findAll() {
    return jRoomRepository.findAll().stream().map(RoomRepository::toDomain).toList();
  }

  public Optional<Room> findById(UUID id) {
    return jRoomRepository.findById(id).map(RoomRepository::toDomain);
  }

  public Room save(Room room) {
    return toDomain(jRoomRepository.save(toEntity(room)));
  }

  public static Room toDomain(JRoom entity) {
    return new Room(entity.getId(), entity.getNumber(), entity.getCapacity());
  }

  public static JRoom toEntity(Room room) {
    return new JRoom(room.getId(), room.getNumber(), room.getCapacity());
  }
}
