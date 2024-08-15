package com.notitficationService.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static com.notitficationService.constants.KafkaConstants.USER_CREATION_TOPIC;
import static com.notitficationService.constants.UserCreationTopicConstants.EMAIL;
import static com.notitficationService.constants.UserCreationTopicConstants.NAME;

@Service
@Slf4j
public class UserCreationConsumer {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    JavaMailSender javaMailSender;

    @KafkaListener(topics=USER_CREATION_TOPIC,groupId="notification-group")
    public void userCreated(String message) throws JsonProcessingException {
      log.info("User created message received: {}",message);

      ObjectNode objectNode = objectMapper.readValue(message,ObjectNode.class);
      String name = objectNode.get(NAME).textValue();
      String email = objectNode.get(EMAIL).textValue();

        SimpleMailMessage mailMessage = new SimpleMailMessage();

        //Inorder to send a mail we require
        //FROM
        //TO
        //SUBJECT
        //BODY

        //CC,BCC(Optional)
        mailMessage.setFrom("wallet-service@gmail.com");
        mailMessage.setTo(email);
        mailMessage.setSubject("Welcome to E-Wallet");
        mailMessage.setText("Hey "+name+" ! Welcome to E-Wallet!");

        javaMailSender.send(mailMessage);

        log.info("User creation mail sent successfully!!");

    }
}
