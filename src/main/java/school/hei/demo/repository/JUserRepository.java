package school.hei.demo.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;
import school.hei.demo.repository.model.JUser;

public interface JUserRepository extends CrudRepository<JUser, UUID> {
  Optional<JUser> findByEmail(String email);
}
