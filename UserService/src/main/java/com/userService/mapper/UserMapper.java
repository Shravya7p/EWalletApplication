package com.userService.mapper;

import com.userService.dto.CreateUserRequest;
import com.userService.enums.UserStatus;
import com.userService.model.User;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserMapper {

    public User mapToUser(CreateUserRequest userRequest){
        return User.builder()
                .name(userRequest.getName())
                .email(userRequest.getEmail())
                .phoneNo(userRequest.getPhoneNo())
                .userIdentificationType(userRequest.getUserIdentificationType())
                .userIdentificationValue(userRequest.getUserIdentificationValue())
                .userStatus(UserStatus.ACTIVE)
                .build();

    }
}
