package com.transactionService.dto;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level= AccessLevel.PRIVATE)
public class InitiateTransactionRequest {

    @NotBlank
    String receiverPhoneNo;

    @Positive
    Double amount;

    String message;

}
