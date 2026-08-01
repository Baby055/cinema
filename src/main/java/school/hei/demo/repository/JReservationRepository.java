package school.hei.demo.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.demo.repository.model.JReservation;

public interface JReservationRepository extends JpaRepository<JReservation, UUID> {
  List<JReservation> findByUserId(UUID userId);

  List<JReservation> findByProjectionId(UUID projectionId);
}
