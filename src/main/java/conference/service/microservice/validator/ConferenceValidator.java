package conference.service.microservice.validator;

import java.time.LocalDate;
import java.util.List;
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
        validateDates(conference.getStartDate(), conference.getEndDate(), true);
        validateLocation(conference.getLocation());
        validateInscriptionPrice(conference.getInscriptionPrice());
        validateSubmissionDeadline(conference.getSubmissionDeadline(), conference.getStartDate(), conference.getEndDate());
        validateTopics(conference.getTopics());
        validateSpeakers(conference.getSpeakers());
    }

    public void validateConferenceForUpdate(Conference conference) {
        validateName(conference.getName());
        validateDates(conference.getStartDate(), conference.getEndDate(), false);
        validateLocation(conference.getLocation());
        validateInscriptionPrice(conference.getInscriptionPrice());
        validateSubmissionDeadline(conference.getSubmissionDeadline(), conference.getStartDate(), conference.getEndDate());
        validateTopics(conference.getTopics());
        validateSpeakers(conference.getSpeakers());
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

    private void validateDates(LocalDate startDate, LocalDate endDate, boolean validateFutureStartDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Conference dates cannot be null");
        }
        if (validateFutureStartDate && !startDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Start date must be after current date");
        }
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
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
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Conference dates cannot be null");
        }
        if (deadline.isBefore(startDate) || deadline.isAfter(endDate)) {
            throw new IllegalArgumentException("Submission deadline must be between start date and end date");
        }
        if (!endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("End date must be after start date");
        }
    }

    private void validateTopics(List<String> topics) {
        if (topics == null || topics.isEmpty()) {
            throw new IllegalArgumentException("Conference topics cannot be null or empty");
        }
        if (topics.stream().anyMatch(topic -> topic == null || topic.isBlank())) {
            throw new IllegalArgumentException("Conference topics cannot contain empty values");
        }
    }

    private void validateSpeakers(List<String> speakers) {
        if (speakers == null || speakers.isEmpty()) {
            throw new IllegalArgumentException("Conference speakers cannot be null or empty");
        }
        if (speakers.stream().anyMatch(speaker -> speaker == null || speaker.isBlank())) {
            throw new IllegalArgumentException("Conference speakers cannot contain empty values");
        }
    }

    private void validateConferenceIdExists(UUID conferenceId) {
        conferenceRepository.findById(conferenceId).orElseThrow(() -> new IllegalArgumentException("Conference with ID " + conferenceId + " does not exist"));
    }
}
