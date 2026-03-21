package conference.service.microservice.listener;

import java.time.LocalDateTime;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import conference.service.microservice.config.RabbitMQConfig;
import conference.service.microservice.dto.enrollment.EnrollmentEventDTO;
import conference.service.microservice.model.ConferenceEnrollmentSummary;
import conference.service.microservice.repository.EnrollmentSummaryRepository;

@Component
public class EnrollmentEventListener {
        private final EnrollmentSummaryRepository summaryRepo;

    public EnrollmentEventListener(EnrollmentSummaryRepository summaryRepo) {
        this.summaryRepo = summaryRepo;
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
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE_CANCELLED)
    public void onEnrollmentCancelled(EnrollmentEventDTO event) {
        summaryRepo.findById(event.getConferenceId()).ifPresent(summary -> {
            int nuevo = Math.max(0, summary.getTotalInscriptions() - 1);
            summary.setTotalInscriptions(nuevo);
            summary.setUpdatedAt(LocalDateTime.now());
            summaryRepo.save(summary);
        });
    }
}
