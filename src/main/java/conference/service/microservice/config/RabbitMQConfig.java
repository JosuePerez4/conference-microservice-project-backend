package conference.service.microservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "enrollment.events";
    public static final String QUEUE_CREATED = "conference.enrollment.created";
    public static final String QUEUE_CANCELLED = "conference.enrollment.cancelled";

    @Bean
    public TopicExchange enrollmentExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue enrollmentCreatedQueue() {
        return new Queue(QUEUE_CREATED, true);
    }

    @Bean
    public Queue enrollmentCancelledQueue() {
        return new Queue(QUEUE_CANCELLED, true);
    }

    @Bean
    public Binding bindingCreated(Queue enrollmentCreatedQueue,
            TopicExchange enrollmentExchange) {
        return BindingBuilder.bind(enrollmentCreatedQueue)
                .to(enrollmentExchange)
                .with("enrollment.created");
    }

    @Bean
    public Binding bindingCancelled(Queue enrollmentCancelledQueue,
            TopicExchange enrollmentExchange) {
        return BindingBuilder.bind(enrollmentCancelledQueue)
                .to(enrollmentExchange)
                .with("enrollment.cancelled");
    }
}
