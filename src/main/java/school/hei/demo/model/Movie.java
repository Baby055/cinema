package school.hei.demo.model;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import school.hei.demo.PojaGenerated;

@PojaGenerated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Movie {

  @EqualsAndHashCode.Include private UUID uuid;

  private String title;

  private Set<Genre> genres;

  private String description;

  private Duration duration;
}
