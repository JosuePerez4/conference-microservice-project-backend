package conference.service.microservice.dto;

import lombok.Data;

@Data
public class ConferenceCreated {
    private String name;
    private String description;
    private String location;
    private boolean virtual;
    private float inscriptionPrice;
    private String startDate;
    private String endDate;
    private String submissionDeadline;
    private String state;
}
