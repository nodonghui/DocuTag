package docuTag.domain.log.service;

import docuTag.domain.log.dto.LogEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class LogProducerService {

    private final KafkaTemplate<String, LogEvent> kafkaTemplate;
    private static final String TOPIC = "frontend-logs";

    public LogProducerService(KafkaTemplate<String, LogEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendLog(LogEvent event) {
        kafkaTemplate.send(TOPIC, event.getUserId(), event);
    }
}