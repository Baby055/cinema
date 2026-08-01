package school.hei.demo.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.demo.repository.model.JProjection;

public interface JProjectionRepository extends JpaRepository<JProjection, UUID> {}
