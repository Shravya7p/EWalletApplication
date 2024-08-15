package com.transactionService.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.transactionService.enums.TransactionStatus;
import com.transactionService.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.transactionService.constants.KafkaConstants.TRANSACTION_UPDATED_TOPIC;
import static com.transactionService.constants.TransactionInititatedConstants.*;
import static com.transactionService.constants.TransactionUpdatedConstants.STATUS;
import static com.transactionService.constants.TransactionUpdatedConstants.STATUSMESSAGE;

@Service
@Slf4j
public class TransactionUpdatedConsumer {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    TransactionRepository transactionRepository;

    @KafkaListener(topics = TRANSACTION_UPDATED_TOPIC,groupId = "transaction-group")
    public void transactionInitiated(String message) throws JsonProcessingException {

        log.info("Transaction updated message received: {} ", message);

        ObjectNode node = mapper.readValue(message, ObjectNode.class);

        String status = node.get(STATUS).textValue();
        String statusMessage = node.get(STATUSMESSAGE).textValue();
        String transactionId = node.get(TRANSACTIONID).textValue();

        transactionRepository.updateTransactionStatus(TransactionStatus.valueOf(status),statusMessage,transactionId);

        log.info("Transaction updated successfully!!");

    }
}
