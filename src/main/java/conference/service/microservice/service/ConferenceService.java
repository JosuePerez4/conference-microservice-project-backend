package conference.service.microservice.service;

import org.springframework.stereotype.Service;

import conference.service.microservice.dto.ConferenceCreated;
import conference.service.microservice.dto.ConferenceRequest;
import conference.service.microservice.mapper.ConferenceMapper;
import conference.service.microservice.model.Conference;
import conference.service.microservice.repository.ConferenceRepository;
import conference.service.microservice.validator.ConferenceValidator;

@Service
public class ConferenceService {
    private final ConferenceRepository conferenceRepository;
    private final ConferenceMapper conferenceMapper;
    private final ConferenceValidator conferenceValidator;

    public ConferenceService(ConferenceRepository conferenceRepository, ConferenceMapper conferenceMapper, ConferenceValidator conferenceValidator) {
        this.conferenceRepository = conferenceRepository;
        this.conferenceMapper = conferenceMapper;
        this.conferenceValidator = conferenceValidator;
    }

    public ConferenceCreated createConference(ConferenceRequest conferenceRequest) {
        Conference conference = conferenceMapper.toConference(conferenceRequest);
        conference.setId(null); // forzar persistencia como nuevo registro
        System.out.println("Conference to be created: " + conference);
        conferenceValidator.validateConference(conference);
        Conference savedConference = conferenceRepository.save(conference);
        return conferenceMapper.toConferenceCreated(savedConference);
    }
}
