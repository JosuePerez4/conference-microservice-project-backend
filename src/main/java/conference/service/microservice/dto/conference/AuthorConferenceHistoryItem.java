package conference.service.microservice.dto.conference;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AuthorConferenceHistoryItem {

    private ConferenceCreated conference;
    private LocalDateTime participatedAt;
}
