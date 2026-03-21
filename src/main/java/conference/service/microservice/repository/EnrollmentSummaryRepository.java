package conference.service.microservice.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import conference.service.microservice.model.ConferenceEnrollmentSummary;

public interface EnrollmentSummaryRepository extends JpaRepository<ConferenceEnrollmentSummary, UUID> {
}
