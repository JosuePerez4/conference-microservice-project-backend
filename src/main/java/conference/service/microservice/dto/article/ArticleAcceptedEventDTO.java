package conference.service.microservice.dto.article;

import java.util.List;
import java.util.UUID;

import lombok.Data;

@Data
public class ArticleAcceptedEventDTO {
    private UUID articleId;
    private UUID conferenceId;
    private List<UUID> authorIds;
}
