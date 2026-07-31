package school.hei.demo.repository.model;

import jakarta.persistence.*;
import lombok.*;
import school.hei.demo.PojaGenerated;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@PojaGenerated
@Entity
@Table(name = "projection")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JProjection {
    @Id
    @GeneratedValue
    private UUID id;

    private Instant datetime;

    private BigDecimal seatPrice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movie_id", nullable = false)
    private JMovie movie;

    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private JRoom room;
}
