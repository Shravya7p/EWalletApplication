package com.userService.dto;

import com.userService.enums.UserIdentificationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserRequest {

    String name;

    String email;

    @NotBlank
    String phoneNo;

    @NotBlank
    String password;

    @NotNull
    UserIdentificationType userIdentificationType;

    @NotNull
    String userIdentificationValue;
}
