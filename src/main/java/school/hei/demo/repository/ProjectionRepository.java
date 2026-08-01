package school.hei.demo.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import school.hei.demo.model.Projection;
import school.hei.demo.repository.model.JMovie;
import school.hei.demo.repository.model.JProjection;
import school.hei.demo.repository.model.JRoom;

@Repository
@AllArgsConstructor
public class ProjectionRepository {
  private JProjectionRepository jProjectionRepository;
  private JMovieRepository jMovieRepository;
  private JRoomRepository jRoomRepository;

  public List<Projection> findAll() {
    return jProjectionRepository.findAll().stream().map(ProjectionRepository::toDomain).toList();
  }

  public Optional<Projection> findById(UUID id) {
    return jProjectionRepository.findById(id).map(ProjectionRepository::toDomain);
  }

  public Projection save(Projection projection) {
    JMovie jMovie =
        jMovieRepository
            .findById(projection.getMovie().getUuid())
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Movie " + projection.getMovie().getUuid() + " not found"));
    JRoom jRoom =
        jRoomRepository
            .findById(projection.getRoom().getId())
            .orElseThrow(
                () ->
                    new IllegalStateException(
                        "Room " + projection.getRoom().getId() + " not found"));
    JProjection toSave =
        new JProjection(
            projection.getId(), projection.getDatetime(), projection.getSeatPrice(), jMovie, jRoom);
    return toDomain(jProjectionRepository.save(toSave));
  }

  public static Projection toDomain(JProjection entity) {
    return new Projection(
        entity.getId(),
        entity.getDatetime(),
        entity.getSeatPrice(),
        MovieRepository.toDomain(entity.getMovie()),
        RoomRepository.toDomain(entity.getRoom()));
  }
}
