package school.hei.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import school.hei.demo.repository.model.JMovie;

import java.util.UUID;

public interface JMovieRepository extends JpaRepository<JMovie, UUID> {
}
