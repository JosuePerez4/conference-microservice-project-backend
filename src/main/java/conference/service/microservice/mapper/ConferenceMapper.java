package conference.service.microservice.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import conference.service.microservice.dto.conference.ConferenceCreated;
import conference.service.microservice.dto.conference.ConferenceRequest;
import conference.service.microservice.dto.conference.ConferenceUpdateRequest;
import conference.service.microservice.enums.ConferenceState;
import conference.service.microservice.model.Conference;

@Component
public class ConferenceMapper {

    private List<String> normalizeStringList(List<String> values) {
        if (values == null) {
            return null;
        }
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .toList();
    }

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
        updateConferenceFromRequest(conference, conferenceRequest);

        return conference;
    }

    public void updateConferenceFromRequest(Conference conference, ConferenceRequest conferenceRequest) {
        conference.setName(conferenceRequest.getName());
        conference.setDescription(conferenceRequest.getDescription());
        conference.setLocation(conferenceRequest.getLocation());
        conference.setVirtual(conferenceRequest.isVirtual());
        conference.setInscriptionPrice(conferenceRequest.getInscriptionPrice());
        if (conferenceRequest.getStartDate() != null && !conferenceRequest.getStartDate().isBlank()) {
            conference.setStartDate(parseDate(conferenceRequest.getStartDate()));
        }
        if (conferenceRequest.getEndDate() != null && !conferenceRequest.getEndDate().isBlank()) {
            conference.setEndDate(parseDate(conferenceRequest.getEndDate()));
        }
        if (conferenceRequest.getSubmissionDeadline() != null && !conferenceRequest.getSubmissionDeadline().isBlank()) {
            conference.setSubmissionDeadline(parseDate(conferenceRequest.getSubmissionDeadline()));
        }
        conference.setTopics(normalizeStringList(conferenceRequest.getTopics()));
        conference.setSpeakers(normalizeStringList(conferenceRequest.getSpeakers()));

        if (conferenceRequest.getState() != null && !conferenceRequest.getState().isBlank()) {
            conference.setState(ConferenceState.valueOf(conferenceRequest.getState().trim().toUpperCase()));
        } else {
            conference.setState(null);
        }
    }

    public void updateConferenceFromUpdateRequest(Conference conference, ConferenceUpdateRequest conferenceRequest) {
        conference.setName(conferenceRequest.getName());
        conference.setDescription(conferenceRequest.getDescription());
        conference.setLocation(conferenceRequest.getLocation());
        conference.setVirtual(conferenceRequest.isVirtual());
        conference.setInscriptionPrice(conferenceRequest.getInscriptionPrice());
        if (conferenceRequest.getStartDate() != null && !conferenceRequest.getStartDate().isBlank()) {
            conference.setStartDate(parseDate(conferenceRequest.getStartDate()));
        }
        if (conferenceRequest.getEndDate() != null && !conferenceRequest.getEndDate().isBlank()) {
            conference.setEndDate(parseDate(conferenceRequest.getEndDate()));
        }
        if (conferenceRequest.getSubmissionDeadline() != null && !conferenceRequest.getSubmissionDeadline().isBlank()) {
            conference.setSubmissionDeadline(parseDate(conferenceRequest.getSubmissionDeadline()));
        }
        if (conferenceRequest.getTopics() != null) {
            conference.setTopics(normalizeStringList(conferenceRequest.getTopics()));
        }
        if (conferenceRequest.getSpeakers() != null) {
            conference.setSpeakers(normalizeStringList(conferenceRequest.getSpeakers()));
        }

        if (conferenceRequest.getState() != null && !conferenceRequest.getState().isBlank()) {
            conference.setState(ConferenceState.valueOf(conferenceRequest.getState().trim().toUpperCase()));
        }
    }

    public ConferenceCreated toConferenceCreated(Conference conference) {
        ConferenceCreated conferenceCreated = new ConferenceCreated();
        conferenceCreated.setId(conference.getId());
        conferenceCreated.setName(conference.getName());
        conferenceCreated.setDescription(conference.getDescription());
        conferenceCreated.setLocation(conference.getLocation());
        conferenceCreated.setVirtual(conference.isVirtual());
        conferenceCreated.setInscriptionPrice(conference.getInscriptionPrice());
        conferenceCreated.setStartDate(conference.getStartDate() != null ? conference.getStartDate().toString() : null);
        conferenceCreated.setEndDate(conference.getEndDate() != null ? conference.getEndDate().toString() : null);
        conferenceCreated.setSubmissionDeadline(conference.getSubmissionDeadline() != null ? conference.getSubmissionDeadline().toString() : null);
        conferenceCreated.setTopics(conference.getTopics());
        conferenceCreated.setSpeakers(conference.getSpeakers());
        String state = conference.getState() != null ? conference.getState().name() : null;
        conferenceCreated.setState(state);
        return conferenceCreated;
    }
}
