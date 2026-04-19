package conference.service.microservice.dto.conference;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class ConferenceCreated {

    private UUID id;
    private String name;
    private String description;
    private String location;
    private boolean virtual;
    private float inscriptionPrice;
    private String startDate;
    private String endDate;
    private String submissionDeadline;
    private List<String> topics;
    private List<String> speakers;
    private String state;
}
