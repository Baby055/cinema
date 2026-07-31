package school.hei.demo.repository.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import school.hei.demo.PojaGenerated;

import java.util.UUID;

@PojaGenerated
@Entity
@Table(name = "seat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JSeat {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    private String number;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private JRoom room;
}
