package conference.service.microservice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import conference.service.microservice.dto.conference.ConferenceCreated;
import conference.service.microservice.dto.conference.ConferenceRequest;
import conference.service.microservice.dto.conference.ConferenceUpdateRequest;
import conference.service.microservice.mapper.ConferenceMapper;
import conference.service.microservice.model.Conference;
import conference.service.microservice.model.ConferenceEnrollmentSummary;
import conference.service.microservice.repository.ConferenceRepository;
import conference.service.microservice.repository.EnrollmentSummaryRepository;
import conference.service.microservice.validator.ConferenceValidator;
import jakarta.persistence.EntityNotFoundException;

@Service
public class ConferenceService {

    private final ConferenceRepository conferenceRepository;
    private final ConferenceMapper conferenceMapper;
    private final ConferenceValidator conferenceValidator;
    private final EnrollmentSummaryRepository summaryRepo;

    public ConferenceService(ConferenceRepository conferenceRepository, ConferenceMapper conferenceMapper, ConferenceValidator conferenceValidator, EnrollmentSummaryRepository summaryRepo) {
        this.conferenceRepository = conferenceRepository;
        this.conferenceMapper = conferenceMapper;
        this.conferenceValidator = conferenceValidator;
        this.summaryRepo = summaryRepo;
    }

    public ConferenceCreated createConference(ConferenceRequest conferenceRequest) {
        Conference conference = conferenceMapper.toConference(conferenceRequest);
        conference.setId(null);
        conferenceValidator.validateConference(conference);
        Conference savedConference = conferenceRepository.save(conference);

        // crear registro en la tabla sombra
        ConferenceEnrollmentSummary summary = new ConferenceEnrollmentSummary();
        summary.setConferenceId(savedConference.getId());
        summary.setTotalInscriptions(0);
        summary.setUpdatedAt(LocalDateTime.now());
        summaryRepo.save(summary);
        return conferenceMapper.toConferenceCreated(savedConference);
    }

    public ConferenceCreated updateConferenceById(UUID id, ConferenceUpdateRequest conferenceRequest) {
        Conference existingConference = conferenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conferencia no encontrada: " + id));

        conferenceMapper.updateConferenceFromUpdateRequest(existingConference, conferenceRequest);
        conferenceValidator.validateConferenceForUpdate(existingConference);

        Conference updatedConference = conferenceRepository.save(existingConference);
        return conferenceMapper.toConferenceCreated(updatedConference);
    }

    public ConferenceCreated getConferenceById(UUID id) {
        Conference conference = conferenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conferencia no encontrada: " + id));
        return conferenceMapper.toConferenceCreated(conference);
    }

    public java.util.List<ConferenceCreated> getAllConferences() {
        return conferenceRepository.findAll().stream()
                .map(conferenceMapper::toConferenceCreated)
                .toList();
    }

    public java.util.List<ConferenceCreated> getConferenceHistory() {
        return conferenceRepository.findAllByOrderByStartDateDesc().stream()
                .map(conferenceMapper::toConferenceCreated)
                .toList();
    }

    @Transactional
    public ConferenceCreated addSponsors(UUID conferenceId, List<String> sponsors) {
        conferenceValidator.validateSponsorsToAdd(sponsors);

        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new EntityNotFoundException("Conferencia no encontrada: " + conferenceId));

        if (conference.getSponsors() == null) {
            conference.setSponsors(new ArrayList<>());
        }

        List<String> normalizedSponsors = sponsors.stream()
                .map(String::trim)
                .distinct()
                .toList();

        for (String sponsor : normalizedSponsors) {
            if (!conference.getSponsors().contains(sponsor)) {
                conference.getSponsors().add(sponsor);
            }
        }

        Conference updatedConference = conferenceRepository.save(conference);
        return conferenceMapper.toConferenceCreated(updatedConference);
    }

    @Transactional
    public void addSpeakerFromAcceptedArticle(UUID conferenceId, UUID authorId) {
        if (authorId == null) {
            throw new IllegalArgumentException("Author ID cannot be null");
        }

        Conference conference = conferenceRepository.findById(conferenceId)
                .orElseThrow(() -> new EntityNotFoundException("Conferencia no encontrada: " + conferenceId));

        if (conference.getSpeakerIds() == null) {
            conference.setSpeakerIds(new ArrayList<>());
        }

        if (!conference.getSpeakerIds().contains(authorId)) {
            conference.getSpeakerIds().add(authorId);
            conferenceRepository.save(conference);
        }
    }

    public boolean deleteConferenceById(UUID id) {
        // Buscar conferencia por ID, lanzar excepción si no existe
        Conference conference = conferenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conferencia no encontrada: " + id));

        // Validar que la conferencia pueda ser eliminada (existencia y número de asistentes)
        conferenceValidator.validateDeleteConference(id);

        // Eliminar la conferencia y su resumen de inscripciones
        conferenceRepository.delete(conference);
        summaryRepo.deleteById(id);
        return true; // Indicar que la eliminación fue exitosa
    }
}
