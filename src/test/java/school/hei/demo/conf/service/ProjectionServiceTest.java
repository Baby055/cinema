package school.hei.demo.conf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.demo.endpoint.rest.exception.NotFoundException;
import school.hei.demo.endpoint.rest.model.SaveProjection;
import school.hei.demo.model.Genre;
import school.hei.demo.model.Movie;
import school.hei.demo.model.Projection;
import school.hei.demo.model.Room;
import school.hei.demo.repository.MovieRepository;
import school.hei.demo.repository.ProjectionRepository;
import school.hei.demo.repository.RoomRepository;
import school.hei.demo.service.ProjectionService;

class ProjectionServiceTest {

  private ProjectionRepository projectionRepository;
  private MovieRepository movieRepository;
  private RoomRepository roomRepository;
  private ProjectionService subject;

  private Movie movie;
  private Room room;

  @BeforeEach
  void setUp() {
    projectionRepository = mock(ProjectionRepository.class);
    movieRepository = mock(MovieRepository.class);
    roomRepository = mock(RoomRepository.class);
    subject = new ProjectionService(projectionRepository, movieRepository, roomRepository);

    movie = new Movie(UUID.randomUUID(), "Dune", Set.of(Genre.SCI_FI), "Sand", null);
    room = new Room(UUID.randomUUID(), "1", 80);
  }

  @Test
  void findAll_returns_every_projection() {
    Projection projection = existingProjection();
    when(projectionRepository.findAll()).thenReturn(List.of(projection));

    assertThat(subject.findAll()).containsExactly(projection);
  }

  @Test
  void findById_not_found_throws_NotFoundException() {
    UUID id = UUID.randomUUID();
    when(projectionRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> subject.findById(id)).isInstanceOf(NotFoundException.class);
  }

  @Test
  void findById_found_returns_the_projection() {
    Projection projection = existingProjection();
    when(projectionRepository.findById(projection.getId())).thenReturn(Optional.of(projection));

    Projection found = subject.findById(projection.getId());

    assertThat(found).isEqualTo(projection);
  }

  @Test
  void save_creation_resolves_movie_and_room_and_generates_an_id() {
    when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
    when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
    when(projectionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    SaveProjection toSave =
        new SaveProjection(
            null,
            Instant.parse("2026-01-01T20:00:00Z"),
            BigDecimal.TEN,
            movie.getId(),
            room.getId());

    Projection saved = subject.save(toSave);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getDatetime()).isEqualTo(Instant.parse("2026-01-01T20:00:00Z"));
    assertThat(saved.getSeatPrice()).isEqualTo(BigDecimal.TEN);
    assertThat(saved.getMovie()).isEqualTo(movie);
    assertThat(saved.getRoom()).isEqualTo(room);
    verify(projectionRepository).save(any());
  }

  @Test
  void save_with_unknown_movie_throws_NotFoundException() {
    UUID unknownMovieId = UUID.randomUUID();
    when(movieRepository.findById(unknownMovieId)).thenReturn(Optional.empty());
    SaveProjection toSave =
        new SaveProjection(null, Instant.now(), BigDecimal.TEN, unknownMovieId, room.getId());

    assertThatThrownBy(() -> subject.save(toSave)).isInstanceOf(NotFoundException.class);
  }

  @Test
  void save_with_unknown_room_throws_NotFoundException() {
    UUID unknownRoomId = UUID.randomUUID();
    when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
    when(roomRepository.findById(unknownRoomId)).thenReturn(Optional.empty());
    SaveProjection toSave =
        new SaveProjection(null, Instant.now(), BigDecimal.TEN, movie.getId(), unknownRoomId);

    assertThatThrownBy(() -> subject.save(toSave)).isInstanceOf(NotFoundException.class);
  }

  @Test
  void save_update_reuses_the_existing_id() {
    Projection existing = existingProjection();
    when(projectionRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
    when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));
    when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
    when(projectionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    SaveProjection toSave =
        new SaveProjection(
            existing.getId(), Instant.now(), BigDecimal.valueOf(15), movie.getId(), room.getId());

    Projection saved = subject.save(toSave);

    assertThat(saved.getId()).isEqualTo(existing.getId());
    assertThat(saved.getSeatPrice()).isEqualTo(BigDecimal.valueOf(15));
    verify(projectionRepository).save(existing);
  }

  private Projection existingProjection() {
    return new Projection(UUID.randomUUID(), Instant.now(), BigDecimal.TEN, movie, room);
  }
}
