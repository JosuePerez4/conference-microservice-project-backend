package conference.service.microservice.model;

import java.security.Timestamp;
import java.util.UUID;

import conference.service.microservice.enums.InscriptionType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inscriptions")
public class Inscription {
    @Id
    private UUID id;
    private UUID conferenceId;
    private UUID user_id;
    private InscriptionType type;
    private Timestamp create_at;
}
