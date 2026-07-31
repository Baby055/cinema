package school.hei.demo.model;

import java.util.UUID;
import lombok.*;
import school.hei.demo.PojaGenerated;

@PojaGenerated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Seat {

  @EqualsAndHashCode.Include private UUID id;

  private String number;

  private Room room;
}
