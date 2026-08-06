package school.hei.demo.conf.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.hei.demo.endpoint.rest.exception.NotFoundException;
import school.hei.demo.endpoint.rest.model.SaveMovie;
import school.hei.demo.model.Genre;
import school.hei.demo.model.Movie;
import school.hei.demo.repository.MovieRepository;
import school.hei.demo.service.MovieService;

class MovieServiceTest {

    private MovieRepository movieRepository;
    private MovieService subject;

    @BeforeEach
    void setUp() {
        movieRepository = mock(MovieRepository.class);
        subject = new MovieService(movieRepository);
    }

    @Test
    void findAll_returns_every_movie() {
        Movie movie = existingMovie();
        when(movieRepository.findAll()).thenReturn(List.of(movie));

        List<Movie> found = subject.findAll();

        assertThat(found).containsExactly(movie);
    }

    @Test
    void findById_found_returns_the_movie() {
        Movie movie = existingMovie();
        when(movieRepository.findById(movie.getId())).thenReturn(Optional.of(movie));

        Movie found = subject.findById(movie.getId());

        assertThat(found).isEqualTo(movie);
    }

    @Test
    void findById_not_found_throws_NotFoundException() {
        UUID id = UUID.randomUUID();
        when(movieRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subject.findById(id)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void save_creation_generates_an_id_and_maps_every_field() {
        when(movieRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        SaveMovie toSave =
                new SaveMovie(null, "Interstellar", Set.of(Genre.SCI_FI), "A space odyssey", 169L);

        Movie saved = subject.save(toSave);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Interstellar");
        assertThat(saved.getGenres()).containsExactly(Genre.SCI_FI);
        assertThat(saved.getDescription()).isEqualTo("A space odyssey");
        assertThat(saved.getDuration()).isEqualTo(Duration.ofMinutes(169));
        verify(movieRepository).save(any());
    }

    @Test
    void save_update_reuses_the_existing_id_and_overwrites_fields() {
        Movie existing = existingMovie();
        when(movieRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(movieRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        SaveMovie toSave =
                new SaveMovie(
                    existing.getId(), "Interstellar 2", Set.of(Genre.DRAMA), "The sequel", 140L);

        Movie saved = subject.save(toSave);

        assertThat(saved.getId()).isEqualTo(existing.getId());
        assertThat(saved.getTitle()).isEqualTo("Interstellar 2");
        assertThat(saved.getGenres()).containsExactly(Genre.DRAMA);
        verify(movieRepository).save(existing);
    }

    @Test
    void save_update_with_unknown_id_throws_NotFoundException() {
        UUID unknownId = UUID.randomUUID();
        when(movieRepository.findById(unknownId)).thenReturn(Optional.empty());
        SaveMovie toSave = new SaveMovie(unknownId, "Ghost", Set.of(Genre.THRILLER), "?", 90L);

        assertThatThrownBy(() -> subject.save(toSave)).isInstanceOf(NotFoundException.class);
    }

    private Movie existingMovie() {
        Movie movie = new Movie();
        movie.setId(UUID.randomUUID());
        movie.setTitle("Dune");
        movie.setGenres(Set.of(Genre.SCI_FI));
        movie.setDescription("Sand, worms, spice");
        movie.setDuration(Duration.ofMinutes(155));
        return movie;
    }
}