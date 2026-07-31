package school.hei.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.demo.repository.model.JRoom;

import java.util.UUID;

public interface JRoomRepository extends JpaRepository<JRoom, UUID> {
}
