package school.hei.demo.endpoint.rest.mapper;

import org.springframework.stereotype.Component;
import school.hei.demo.endpoint.rest.model.MovieRest;
import school.hei.demo.model.Movie;

@Component
public class MovieMapper {
    public MovieRest toRest(Movie movie) {
        return new MovieRest(
                movie.getUuid(),
                movie.getTitle(),
                movie.getGenres(),
                movie.getDescription(),
                movie.getDuration().toMinutes()
        );
    }
}
