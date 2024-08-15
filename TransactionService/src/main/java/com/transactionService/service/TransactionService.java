package com.transactionService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.transactionService.clients.UserServiceClient;
import com.transactionService.dto.InitiateTransactionRequest;
import com.transactionService.enums.TransactionStatus;
import com.transactionService.model.Transaction;
import com.transactionService.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static com.transactionService.constants.KafkaConstants.TRANSACTION_INITIATED_TOPIC;
import static com.transactionService.constants.TransactionInititatedConstants.*;

@Service
@Slf4j
public class TransactionService implements UserDetailsService {

    @Autowired
    UserServiceClient userServiceClient;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public UserDetails loadUserByUsername(String phoneNo) throws UsernameNotFoundException {

        String auth = "transaction-service:transaction-service";
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes());

        String authValue = "Basic "+new String(encodedAuth);

        ObjectNode node = userServiceClient.getUser(phoneNo,authValue);

        log.info("user fetched : {} ",node);

        if(node == null){
            throw new UsernameNotFoundException("User does not exists!");
        }

        ArrayNode authorities = (ArrayNode) node.get("authorities");

        final List<GrantedAuthority> authorityList = new ArrayList<>();

        authorities.iterator().forEachRemaining(jsonNode -> {
            authorityList.add(new SimpleGrantedAuthority(jsonNode.get("authority").textValue()));
        });

        User user = new User(node.get("phoneNo").textValue(),node.get("password").textValue(),authorityList);
        return user;
    }

    public String initiateTransaction(String senderPhoneNo,InitiateTransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .senderPhoneNo(senderPhoneNo)
                .receiverPhoneNo(request.getReceiverPhoneNo())
                .amount(request.getAmount())
                .purpose(request.getMessage())
                .status(TransactionStatus.INITIATED).build(); //this would be the data we would be

        transactionRepository.save(transaction);
        // storing in our database

        log.info("transaction saved!");


        //publish data to Kafka
        //senderPhoneNo, receiverPhoneNo, amount, transactionid these fields should be send to wallet service
        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put(SENDERPHONENO,transaction.getSenderPhoneNo());
        objectNode.put(RECEIVERPHONENO,transaction.getReceiverPhoneNo());
        objectNode.put(AMOUNT,transaction.getAmount());
        objectNode.put(TRANSACTIONID,transaction.getTransactionId());

        String kafkaMessage=objectNode.toString();
        kafkaTemplate.send(TRANSACTION_INITIATED_TOPIC,kafkaMessage);

        log.info("Published message to kafka: {}",kafkaMessage);


        return transaction.getTransactionId();
    }

    public List<Transaction> findTransactions(String senderPhoneNo,Integer pageNo, Integer limit) {

        Pageable pageable = PageRequest.of(pageNo,limit);
        return transactionRepository.findBySenderPhoneNo(senderPhoneNo,pageable);
    }
}
