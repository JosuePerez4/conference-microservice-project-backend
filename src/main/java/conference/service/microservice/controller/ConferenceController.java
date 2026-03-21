package conference.service.microservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import conference.service.microservice.dto.ConferenceCreated;
import conference.service.microservice.dto.ConferenceRequest;
import conference.service.microservice.service.ConferenceService;
import jakarta.validation.Valid;
import java.util.UUID;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteConference(@PathVariable UUID id) {
        boolean deleted = conferenceService.deleteConferenceById(id);
        if (deleted) {
            // Devolver String diciendo que se eliminó la conferencia con el ID especificado
            return ResponseEntity.ok("Conference deleted successfully");
        } else {
            // Devolver String diciendo que NO se eliminó la conferencia con el ID especificado
            return ResponseEntity.ok("Conference could not be deleted because there are attendees registered");
        }
    }
}
