package school.hei.demo.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.demo.repository.model.JMovie;

public interface JMovieRepository extends JpaRepository<JMovie, UUID> {}
