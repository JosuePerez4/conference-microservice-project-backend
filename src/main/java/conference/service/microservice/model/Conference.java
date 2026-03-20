package conference.service.microservice.model;

import java.time.LocalDate;
import java.util.UUID;

import conference.service.microservice.enums.ConferenceState;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conference")
public class Conference {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    private String name;
    private String description;
    private String location;
    private boolean virtual;
    private float inscriptionPrice;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate submissionDeadline;
    @Enumerated(EnumType.STRING)
    private ConferenceState state;
}