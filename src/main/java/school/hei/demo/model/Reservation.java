package school.hei.demo.model;

import java.time.Instant;
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
public class Reservation {

  @EqualsAndHashCode.Include private UUID id;

  private Instant createdAt;

  private ReservationStatus status;

  private Projection projection;

  private User user;

  private Set<Seat> seats;
}
