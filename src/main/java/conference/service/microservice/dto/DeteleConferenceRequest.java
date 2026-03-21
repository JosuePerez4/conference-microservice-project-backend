package conference.service.microservice.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class DeteleConferenceRequest {
    private UUID conferenceId;
}
