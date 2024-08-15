package com.wallet.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.wallet.model.Wallet;
import com.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static com.wallet.constants.KafkaConstants.TRANSACTION_INITIATED_TOPIC;
import static com.wallet.constants.KafkaConstants.TRANSACTION_UPDATED_TOPIC;
import static com.wallet.constants.TransactionInititatedConstants.*;
import static com.wallet.constants.TransactionUpdatedConstants.STATUS;
import static com.wallet.constants.TransactionUpdatedConstants.STATUSMESSAGE;
import static com.wallet.constants.UserCreationTopicConstants.PHONENO;
import static com.wallet.constants.UserCreationTopicConstants.USERID;

@Service
@Slf4j
public class TransactionInitiatedConsumer {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    KafkaTemplate kafkaTemplate;


    @KafkaListener(topics = TRANSACTION_INITIATED_TOPIC,groupId = "wallet-group")
    public void transactionInitiated(String message) throws JsonProcessingException {

        log.info("Transaction initiated message received: {} ", message);

        ObjectNode node = mapper.readValue(message, ObjectNode.class);

        String senderPhoneNo = node.get(SENDERPHONENO).textValue();
        String receiverPhoneNo = node.get(RECEIVERPHONENO).textValue();
        String transactionId = node.get(TRANSACTIONID).textValue();
        Double amount = node.get(AMOUNT).doubleValue();

        //fetching the wallets of the user

        String status;
        String statusMessage;
        Wallet senderWallet = walletRepository.findByPhoneNo(senderPhoneNo);
        Wallet receiverWallet = walletRepository.findByPhoneNo(receiverPhoneNo);

        if(senderWallet  == null){
            log.info("Sender wallet is not present");
            status="FAILED";
            statusMessage="sender wallet does not exist in our repository";
        } else if(receiverWallet == null){
            log.info("Receiver wallet is not present");
            status="FAILED";
            statusMessage = "receiver wallet does not exist in our repository";
        } else if(amount > senderWallet.getBalance()){
            log.info("Amount trying to send is greater than the balance amount");
            status="FAILED";
            statusMessage="amount to be sent is greater than the amount in the sender's wallet";
        } else{
            //successful transaction
            log.info("successful!");
            status="SUCCESSFUL";
            statusMessage="transaction is successful!";
            updateWallet(senderWallet,receiverWallet,amount);
            log.info("Wallet updated!");
        }

        //publish message back to Kafka
        sendMessageToKafka(transactionId,status,statusMessage);

    }

    private void sendMessageToKafka(String transactionId, String status, String statusMessage) {

        ObjectNode node = mapper.createObjectNode();

        node.put(STATUS,status);
        node.put(STATUSMESSAGE,statusMessage);
        node.put(TRANSACTIONID,transactionId);

        kafkaTemplate.send(TRANSACTION_UPDATED_TOPIC,node.toString());


    }

    @Transactional
    public void updateWallet(Wallet senderWallet,
                             Wallet receiverWallet,
                             Double amount){

        walletRepository.updateWallet(senderWallet.getPhoneNo(), -amount);
        walletRepository.updateWallet(receiverWallet.getPhoneNo(),amount);

    }

}
