package conference.service.microservice.model;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "conference_enrollment_summary")
public class ConferenceEnrollmentSummary {

    @Id
    @Column(name = "conference_id")
    private UUID conferenceId;

    @Column(name = "total_inscriptions")
    private int totalInscriptions = 0;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    

    public boolean hasEnrollments() {
        return totalInscriptions > 0;
    }
}
