package conference.service.microservice.mapper;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.stereotype.Component;

import conference.service.microservice.dto.conference.ConferenceCreated;
import conference.service.microservice.dto.conference.ConferenceRequest;
import conference.service.microservice.enums.ConferenceState;
import conference.service.microservice.model.Conference;

@Component
public class ConferenceMapper {

    private LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }

        dateString = dateString.trim();

        // Prefiere ISO (YYYY-MM-DD)
        try {
            return LocalDate.parse(dateString);
        } catch (Exception ignored) {
        }

        // Acepta DD-MM-YYYY si llega en ese formato
        try {
            return LocalDate.parse(dateString, java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (Exception ignored) {
        }

        // Lanza excepción clara para manejar en el controlador si no es un formato válido.
        throw new IllegalArgumentException("Formato de fecha inválido: " + dateString + ". Use YYYY-MM-DD o DD-MM-YYYY.");
    }

    public Conference toConference(ConferenceRequest conferenceRequest) {
        Conference conference = new Conference();
        conference.setId(UUID.randomUUID());
        conference.setName(conferenceRequest.getName());
        conference.setDescription(conferenceRequest.getDescription());
        conference.setLocation(conferenceRequest.getLocation());
        conference.setVirtual(conferenceRequest.isVirtual());
        conference.setInscriptionPrice(conferenceRequest.getInscriptionPrice());

        conference.setStartDate(parseDate(conferenceRequest.getStartDate()));
        conference.setEndDate(parseDate(conferenceRequest.getEndDate()));
        conference.setSubmissionDeadline(parseDate(conferenceRequest.getSubmissionDeadline()));

        if (conferenceRequest.getState() != null && !conferenceRequest.getState().isBlank()) {
            conference.setState(ConferenceState.valueOf(conferenceRequest.getState().trim().toUpperCase()));
        } else {
            conference.setState(null);
        }

        return conference;
    }

    public ConferenceCreated toConferenceCreated(Conference conference) {
        ConferenceCreated conferenceCreated = new ConferenceCreated();
        conferenceCreated.setName(conference.getName());
        conferenceCreated.setDescription(conference.getDescription());
        conferenceCreated.setLocation(conference.getLocation());
        conferenceCreated.setVirtual(conference.isVirtual());
        conferenceCreated.setInscriptionPrice(conference.getInscriptionPrice());
        conferenceCreated.setStartDate(conference.getStartDate() != null ? conference.getStartDate().toString() : null);
        conferenceCreated.setEndDate(conference.getEndDate() != null ? conference.getEndDate().toString() : null);
        conferenceCreated.setSubmissionDeadline(conference.getSubmissionDeadline() != null ? conference.getSubmissionDeadline().toString() : null);
        String state = conference.getState() != null ? conference.getState().name() : null;
        conferenceCreated.setState(state);
        return conferenceCreated;
    }
}
