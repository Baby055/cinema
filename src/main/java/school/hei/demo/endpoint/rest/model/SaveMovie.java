package school.hei.demo.endpoint.rest.model;

import java.util.Set;
import java.util.UUID;
import school.hei.demo.model.Genre;

public record SaveMovie(
    UUID id, String title, Set<Genre> genres, String description, long durationInMinutes) {}
