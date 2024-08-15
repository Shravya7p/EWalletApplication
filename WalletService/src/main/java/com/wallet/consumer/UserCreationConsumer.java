package com.wallet.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wallet.model.Wallet;

import com.wallet.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.wallet.constants.KafkaConstants.USER_CREATION_TOPIC;
import static com.wallet.constants.UserCreationTopicConstants.PHONENO;
import static com.wallet.constants.UserCreationTopicConstants.USERID;

@Service
@Slf4j
public class UserCreationConsumer {

    @Autowired
    ObjectMapper mapper;

    @Value("${wallet.initial.amount}")
    Double walletAmount;

    @Autowired
    WalletRepository walletRepository;

    @KafkaListener(topics = USER_CREATION_TOPIC,groupId = "wallet-group")
    public void userCreated(String message) throws JsonProcessingException{

        log.info("User created message received: {} ",message);

        ObjectNode node = mapper.readValue(message, ObjectNode.class);
        String phoneNo = node.get(PHONENO).textValue();
        Integer userId = node.get(USERID).intValue();

        Wallet wallet = Wallet.builder()
                .phoneNo(phoneNo)
                .userId(userId)
                        .balance(walletAmount).build();

        walletRepository.save(wallet);

        log.info("wallet saved for user : {} ",userId);


    }

}
