package school.hei.demo.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.demo.repository.model.JSeat;

public interface JSeatRepository extends JpaRepository<JSeat, UUID> {

  List<JSeat> findByRoomId(UUID roomId);

  List<JSeat> findByIdIn(List<UUID> ids);
}
