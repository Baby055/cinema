package school.hei.demo.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import school.hei.demo.endpoint.rest.exception.NotFoundException;
import school.hei.demo.endpoint.rest.model.SaveProjection;
import school.hei.demo.repository.MovieRepository;
import school.hei.demo.repository.ProjectionRepository;
import school.hei.demo.repository.RoomRepository;
import school.hei.demo.model.Projection;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ProjectionService {
    private final ProjectionRepository projectionRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;

    public List<Projection> findAll() {
        return projectionRepository.findAll();
    }

    public Projection findById(UUID id) {
        return projectionRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Projection " + id + " not found"));
    }

    @Transactional
    public Projection save(SaveProjection toSave) {
        Projection projection =
                toSave.id() == null
                        ? new Projection()
                        : projectionRepository.findById(toSave.id()).orElse(new Projection());
        if (projection.getId() == null) {
            projection.setId(toSave.id() == null ? UUID.randomUUID() : toSave.id());
        }
        projection.setDatetime(toSave.datetime());
        projection.setSeatPrice(toSave.seatPrice());
        projection.setMovie(
                movieRepository
                        .findById(toSave.movieId())
                        .orElseThrow(() -> new NotFoundException("Movie " + toSave.movieId() + " not found")));
        projection.setRoom(
                roomRepository
                        .findById(toSave.roomId())
                        .orElseThrow(() -> new NotFoundException("Room " + toSave.roomId() + " not found")));
        return projectionRepository.save(projection);
    }
}
