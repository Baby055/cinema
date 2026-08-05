package school.hei.demo.repository.model;

import jakarta.persistence.*;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import school.hei.demo.PojaGenerated;
import school.hei.demo.model.Genre;

@PojaGenerated
@Entity
@Table(name = "movie")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JMovie {
  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(name = "id")
  private UUID id;

  private String title;

  @ElementCollection(targetClass = Genre.class)
  @CollectionTable(name = "movie_genre", joinColumns = @JoinColumn(name = "movie_id"))
  @Enumerated(EnumType.STRING)
  private Set<Genre> genres;

  private String description;

  private Duration duration;
}
