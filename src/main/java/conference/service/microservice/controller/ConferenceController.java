package conference.service.microservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import conference.service.microservice.dto.ConferenceCreated;
import conference.service.microservice.dto.ConferenceRequest;
import conference.service.microservice.service.ConferenceService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/conferences")
public class ConferenceController {

    private final ConferenceService conferenceService;

    public ConferenceController(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @PostMapping("/create")
    public ResponseEntity<ConferenceCreated> createConference(@Valid @RequestBody ConferenceRequest conference) {
        ConferenceCreated createdConference = conferenceService.createConference(conference);
        return ResponseEntity.ok(createdConference);
    }
}
