package conference.service.microservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import conference.service.microservice.model.Inscription;

public interface InscriptionRepository extends JpaRepository<Inscription, UUID> {
    int countByConferenceId(UUID conferenceId);
}
