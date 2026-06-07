package conference.service.microservice.dto.conference;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AddSponsorsRequest {

    @NotEmpty(message = "La lista de patrocinadores no puede estar vacía")
    private List<String> sponsors;
}
