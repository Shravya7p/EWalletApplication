package com.transactionService.model;

import com.transactionService.enums.TransactionStatus;
import jakarta.persistence.*;
import jdk.jfr.DataAmount;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level= AccessLevel.PRIVATE)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String senderPhoneNo;

    String receiverPhoneNo;

    String transactionId;  //this id would be returned to the user

    Double amount;

    String purpose;

    String transactionStatusMessage;

    @Enumerated(value=EnumType.STRING)
    TransactionStatus status;

    @CreationTimestamp
    Date createdOn;

    @UpdateTimestamp
    Date updatedOn;

}
