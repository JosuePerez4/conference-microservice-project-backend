package conference.service.microservice.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import conference.service.microservice.model.AuthorConferenceParticipation;

public interface AuthorParticipationRepository extends JpaRepository<AuthorConferenceParticipation, AuthorConferenceParticipation.AuthorConferenceParticipationId> {

    List<AuthorConferenceParticipation> findByUserIdOrderByParticipatedAtDesc(UUID userId);
}
