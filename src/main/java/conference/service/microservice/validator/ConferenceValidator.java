package conference.service.microservice.validator;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import conference.service.microservice.model.Conference;

@Component
public class ConferenceValidator {

    public void validateConference(Conference conference) {
        validateName(conference.getName());
        validateDates(conference.getStartDate(), conference.getEndDate());
        validateLocation(conference.getLocation());
        validateInscriptionPrice(conference.getInscriptionPrice());
        validateSubmissionDeadline(conference.getSubmissionDeadline(), conference.getStartDate(), conference.getEndDate());
    }

    public void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Conference name cannot be null or empty");
        }
    }

    public void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Conference dates cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }
    }

    public void validateLocation(String location) {
        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Conference location cannot be null or empty");
        }
    }

    public void validateInscriptionPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Inscription price cannot be negative");
        }
    }

    public void validateSubmissionDeadline(LocalDate deadline, LocalDate startDate, LocalDate endDate) {
        if (deadline == null) {
            throw new IllegalArgumentException("Submission deadline cannot be null");
        }
        if (deadline.isBefore(startDate) || deadline.isAfter(endDate)) {
            throw new IllegalArgumentException("Submission deadline must be between start and end date");
        }
    }
}
