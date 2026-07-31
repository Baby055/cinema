package school.hei.demo.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;
import school.hei.demo.PojaGenerated;

@PojaGenerated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Projection {

  @EqualsAndHashCode.Include private UUID id;

  private Instant datetime;

  private BigDecimal setPrice;

  private Movie movie;

  private Room room;
}
