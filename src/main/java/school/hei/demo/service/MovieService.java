package school.hei.demo.service;

import jakarta.transaction.Transactional;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.demo.endpoint.rest.NotFoundException;
import school.hei.demo.endpoint.rest.model.SaveMovie;
import school.hei.demo.model.Movie;
import school.hei.demo.repository.MovieRepository;

@Service
@AllArgsConstructor
public class MovieService {

  private final MovieRepository movieRepository;

  public List<Movie> findAll() {
    return movieRepository.findAll();
  }

  public Movie findById(UUID id) {
    return movieRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException("Movie " + id + " not found"));
  }

  @Transactional
  public Movie save(SaveMovie toSave) {
    Movie movie =
        toSave.id() == null
            ? new Movie()
            : movieRepository.findById(toSave.id()).orElse(new Movie());
    if (movie.getUuid() == null) {
      movie.setUuid(toSave.id() == null ? UUID.randomUUID() : toSave.id());
    }
    movie.setTitle(toSave.title());
    movie.setGenres(toSave.genres());
    movie.setDescription(toSave.description());
    movie.setDuration(Duration.ofMinutes(toSave.durationInMinutes()));
    return movieRepository.save(movie);
  }
}
