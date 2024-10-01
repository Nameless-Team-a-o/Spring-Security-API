package com.nameless.entity.result;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nameless.dto.ResultResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
@Component
public class ResultConsumer {

    private final ObjectMapper objectMapper;
    private final ResultService resultService;
    private static final Logger logger = LoggerFactory.getLogger(ResultConsumer.class);

    @RabbitListener(queues = "resultQueue") // Specify the queue to listen to
    public void receiveResult(String resultJson) {
        try {
            ResultResponseDTO resultResponse = objectMapper.readValue(resultJson, ResultResponseDTO.class);
            logger.info("Received result: {}", resultResponse);
            resultService.processResult(resultResponse);
        } catch (Exception e) {
            logger.error("Error processing result: {}", e.getMessage());
        }
    }
}
