package conference.service.microservice.validator;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Component;

import conference.service.microservice.model.Conference;
import conference.service.microservice.repository.InscriptionRepository;
import conference.service.microservice.repository.ConferenceRepository;

@Component
public class ConferenceValidator {

    private InscriptionRepository inscription;
    private ConferenceRepository conferenceRepository;

    public ConferenceValidator(InscriptionRepository inscription, ConferenceRepository conferenceRepository) {
        this.inscription = inscription;
        this.conferenceRepository = conferenceRepository;
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
        int numberOfAttendees = inscription.countByConferenceId(conferenceId);
        if (numberOfAttendees > 0) {
            throw new IllegalStateException("Cannot delete conference with existing inscriptions");
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
