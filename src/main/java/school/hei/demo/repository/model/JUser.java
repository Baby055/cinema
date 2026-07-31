package school.hei.demo.repository.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import school.hei.demo.PojaGenerated;
import school.hei.demo.model.UserRole;

import java.time.LocalDate;
import java.util.UUID;

@PojaGenerated
@Entity
@Table(name = "\"user\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JUser {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private LocalDate birthDate;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;
}
