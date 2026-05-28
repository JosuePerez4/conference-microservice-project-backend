package conference.service.microservice.listener;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import conference.service.microservice.config.RabbitMQConfig;
import conference.service.microservice.dto.enrollment.EnrollmentEventDTO;
import conference.service.microservice.enums.InscriptionType;
import conference.service.microservice.model.AuthorConferenceParticipation;
import conference.service.microservice.model.ConferenceEnrollmentSummary;
import conference.service.microservice.repository.AuthorParticipationRepository;
import conference.service.microservice.repository.EnrollmentSummaryRepository;

@Component
public class EnrollmentEventListener {
    private final EnrollmentSummaryRepository summaryRepo;
    private final AuthorParticipationRepository authorParticipationRepo;

    public EnrollmentEventListener(
            EnrollmentSummaryRepository summaryRepo,
            AuthorParticipationRepository authorParticipationRepo) {
        this.summaryRepo = summaryRepo;
        this.authorParticipationRepo = authorParticipationRepo;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CREATED)
    public void onEnrollmentCreated(EnrollmentEventDTO event) {
        summaryRepo.findById(event.getConferenceId()).ifPresentOrElse(
            summary -> {
                summary.setTotalInscriptions(summary.getTotalInscriptions() + 1);
                summary.setUpdatedAt(LocalDateTime.now());
                summaryRepo.save(summary);
            },
            () -> {
                // Primera inscripción de esta conferencia
                ConferenceEnrollmentSummary summary = new ConferenceEnrollmentSummary();
                summary.setConferenceId(event.getConferenceId());
                summary.setTotalInscriptions(1);
                summary.setUpdatedAt(LocalDateTime.now());
                summaryRepo.save(summary);
            }
        );

        if (isAuthorEnrollment(event)) {
            recordAuthorParticipation(event);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CANCELLED)
    public void onEnrollmentCancelled(EnrollmentEventDTO event) {
        if (isAuthorEnrollment(event)) {
            removeAuthorParticipation(event);
        }

        summaryRepo.findById(event.getConferenceId()).ifPresent(summary -> {
            int nuevo = Math.max(0, summary.getTotalInscriptions() - 1);
            summary.setTotalInscriptions(nuevo);
            summary.setUpdatedAt(LocalDateTime.now());
            summaryRepo.save(summary);
        });
    }

    private boolean isAuthorEnrollment(EnrollmentEventDTO event) {
        if (event.getTipo() == null || event.getTipo().isBlank()) {
            return false;
        }
        return InscriptionType.AUTOR.name().equalsIgnoreCase(event.getTipo().trim());
    }

    private void recordAuthorParticipation(EnrollmentEventDTO event) {
        if (event.getUserId() == null || event.getConferenceId() == null) {
            return;
        }

        AuthorConferenceParticipation participation = new AuthorConferenceParticipation();
        participation.setUserId(event.getUserId());
        participation.setConferenceId(event.getConferenceId());
        participation.setParticipatedAt(LocalDateTime.now());
        authorParticipationRepo.save(participation);
    }

    private void removeAuthorParticipation(EnrollmentEventDTO event) {
        if (event.getUserId() == null || event.getConferenceId() == null) {
            return;
        }

        authorParticipationRepo.deleteById(
                new AuthorConferenceParticipation.AuthorConferenceParticipationId(
                        event.getUserId(),
                        event.getConferenceId()));
    }
}
