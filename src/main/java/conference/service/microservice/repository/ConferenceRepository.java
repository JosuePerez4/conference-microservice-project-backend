package conference.service.microservice.repository;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import conference.service.microservice.model.Conference;

@Repository
public interface ConferenceRepository extends JpaRepository<Conference,UUID> {
}
