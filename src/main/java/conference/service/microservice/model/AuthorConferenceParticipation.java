package conference.service.microservice.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(AuthorConferenceParticipation.AuthorConferenceParticipationId.class)
@Table(name = "author_conference_participation")
public class AuthorConferenceParticipation {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Id
    @Column(name = "conference_id", nullable = false)
    private UUID conferenceId;

    @Column(name = "participated_at", nullable = false)
    private LocalDateTime participatedAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorConferenceParticipationId implements Serializable {
        private UUID userId;
        private UUID conferenceId;
    }
}
