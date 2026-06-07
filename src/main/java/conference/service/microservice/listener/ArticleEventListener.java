package conference.service.microservice.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import conference.service.microservice.config.RabbitMQConfig;
import conference.service.microservice.dto.article.ArticleAcceptedEventDTO;
import conference.service.microservice.service.ConferenceService;
import jakarta.persistence.EntityNotFoundException;

@Component
public class ArticleEventListener {

    private static final Logger log = LoggerFactory.getLogger(ArticleEventListener.class);

    private final ConferenceService conferenceService;

    public ArticleEventListener(ConferenceService conferenceService) {
        this.conferenceService = conferenceService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_ARTICLE_ACCEPTED)
    public void onArticleAccepted(ArticleAcceptedEventDTO event) {
        if (event == null || event.getConferenceId() == null) {
            log.warn("Evento article.accepted inválido: {}", event);
            return;
        }

        List<UUID> authorIds = resolveAuthorIds(event);
        if (authorIds.isEmpty()) {
            log.warn("Evento article.accepted sin autores para artículo {}", event.getArticleId());
            return;
        }

        try {
            for (UUID authorId : authorIds) {
                conferenceService.addSpeakerFromAcceptedArticle(event.getConferenceId(), authorId);
            }
            log.info("Speakers {} agregados a la conferencia {} por artículo aceptado {}",
                    authorIds, event.getConferenceId(), event.getArticleId());
        } catch (EntityNotFoundException ex) {
            log.error("No se pudo agregar speakers: conferencia {} no existe", event.getConferenceId());
        }
    }

    private static List<UUID> resolveAuthorIds(ArticleAcceptedEventDTO event) {
        if (event.getAuthorIds() != null && !event.getAuthorIds().isEmpty()) {
            return event.getAuthorIds().stream().distinct().toList();
        }
        return new ArrayList<>();
    }
}
