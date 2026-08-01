package school.hei.demo.repository.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import school.hei.demo.PojaGenerated;
import school.hei.demo.model.ReservationStatus;

@PojaGenerated
@Entity
@Table(name = "reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JReservation {
  @Id @GeneratedValue @UuidGenerator private UUID id;

  private Instant createdAt;

  @Enumerated(EnumType.STRING)
  private ReservationStatus status;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "projection_id", nullable = false)
  private JProjection projection;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private JUser user;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "reservation_seat",
      joinColumns = @JoinColumn(name = "reservation_id"),
      inverseJoinColumns = @JoinColumn(name = "seat_id"))
  private Set<JSeat> seats;
}
