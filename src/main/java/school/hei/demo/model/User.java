package school.hei.demo.model;

import java.time.LocalDate;
import java.util.UUID;
import lombok.*;
import school.hei.demo.PojaGenerated;

@PojaGenerated
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

  @EqualsAndHashCode.Include private UUID id;

  private String firstName;

  private String lastName;

  private LocalDate birthDate;

  private String email;

  private String password;

  private String phone;

  private UserRole role;
}
