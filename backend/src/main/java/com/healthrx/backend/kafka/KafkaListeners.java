package com.healthrx.backend.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthrx.backend.service.impl.MailService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaListeners {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final MailService mailService;
    private final Map<String, String> templateDispatcher;

    @SneakyThrows
    @KafkaListener(topics = "mails", groupId = "1")
    void listener(String message) throws JsonProcessingException {
        KafkaReceiveModel kafkaReceiveModel = objectMapper.readValue(message, KafkaReceiveModel.class);
        log.info("Kafka message received: {}", kafkaReceiveModel);

        Context context = new Context();
        kafkaReceiveModel.getData().forEach(context::setVariable);

        mailService.sendEmailToMultiple(
                kafkaReceiveModel.getEmails(),
                kafkaReceiveModel.getSubject(),
                templateDispatcher.get(kafkaReceiveModel.getStrategy()),
                context
        );
    }
}
