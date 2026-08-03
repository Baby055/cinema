package school.hei.demo.endpoint.rest.model;

import school.hei.demo.model.Genre;

import java.util.Set;
import java.util.UUID;

public record MovieRest(
        UUID id, String title, Set<Genre> genres, String description, long durationInMinutes) {}
