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
public class Room {

  @EqualsAndHashCode.Include private UUID id;

  private String number;

  private int capacity;
}
