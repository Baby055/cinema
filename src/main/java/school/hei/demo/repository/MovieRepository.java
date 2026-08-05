package school.hei.demo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.demo.model.Movie;
import school.hei.demo.repository.model.JMovie;

@Repository
@AllArgsConstructor
public class MovieRepository {
  private final JMovieRepository jMovieRepository;

  public List<Movie> findAll() {
    return jMovieRepository.findAll().stream().map(MovieRepository::toDomain).toList();
  }

  public Optional<Movie> findById(UUID id) {
    return jMovieRepository.findById(id).map(MovieRepository::toDomain);
  }

  public Movie save(Movie movie) {
    return toDomain(jMovieRepository.save(toEntity(movie)));
  }

  public static Movie toDomain(JMovie entity) {
    return new Movie(
        entity.getId(),
        entity.getTitle(),
        entity.getGenres(),
        entity.getDescription(),
        entity.getDuration());
  }

  public static JMovie toEntity(Movie movie) {
    return new JMovie(
        movie.getId(),
        movie.getTitle(),
        movie.getGenres(),
        movie.getDescription(),
        movie.getDuration());
  }
}
