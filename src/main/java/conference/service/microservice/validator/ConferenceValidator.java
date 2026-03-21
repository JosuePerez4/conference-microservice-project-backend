package conference.service.microservice.validator;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Component;

import conference.service.microservice.model.Conference;
import conference.service.microservice.model.ConferenceEnrollmentSummary;
import conference.service.microservice.repository.ConferenceRepository;
import conference.service.microservice.repository.EnrollmentSummaryRepository;

@Component
public class ConferenceValidator {

    private final ConferenceRepository conferenceRepository;
    private final EnrollmentSummaryRepository summaryRepo;

    public ConferenceValidator(ConferenceRepository conferenceRepository, EnrollmentSummaryRepository summaryRepo) {
        this.conferenceRepository = conferenceRepository;
        this.summaryRepo = summaryRepo;
    }

    public void validateConference(Conference conference) {
        validateName(conference.getName());
        validateDates(conference.getStartDate(), conference.getEndDate());
        validateLocation(conference.getLocation());
        validateInscriptionPrice(conference.getInscriptionPrice());
        validateSubmissionDeadline(conference.getSubmissionDeadline(), conference.getStartDate(), conference.getEndDate());
    }

    public boolean validateDeleteConference(UUID conferenceId) {
        validateConferenceIdExists(conferenceId);
        return validateNumberOfAttenders(conferenceId);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Conference name cannot be null or empty");
        }
    }

    private void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Conference dates cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }

    private void validateLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Conference location cannot be null or empty");
        }
    }

    private void validateInscriptionPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Inscription price cannot be negative");
        }
    }

    private boolean validateNumberOfAttenders(UUID conferenceId) {
        boolean tieneInscripciones = summaryRepo.findById(conferenceId)
                .map(ConferenceEnrollmentSummary::hasEnrollments)
                .orElse(false);

        if (tieneInscripciones) {
            throw new IllegalStateException("Cannot delete conference with existing enrollments");
        }
        return true;
    }

    private void validateSubmissionDeadline(LocalDate deadline, LocalDate startDate, LocalDate endDate) {
        if (deadline == null) {
            throw new IllegalArgumentException("Submission deadline cannot be null");
        }
        if (deadline.isBefore(startDate) || deadline.isAfter(endDate)) {
            throw new IllegalArgumentException("Submission deadline must be between start and end date");
        }
    }

    private void validateConferenceIdExists(UUID conferenceId) {
        conferenceRepository.findById(conferenceId).orElseThrow(() -> new IllegalArgumentException("Conference with ID " + conferenceId + " does not exist"));
    }
}
