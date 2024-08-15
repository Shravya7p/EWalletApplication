package com.userService.controller;

import com.userService.dto.CreateUserRequest;
import com.userService.model.User;
import com.userService.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {


    @Autowired
    UserService userService;


    @PostMapping("/user")
    public User createUser(@RequestBody @Valid CreateUserRequest userRequest){
           return userService.createUser(userRequest);
    }

    @GetMapping("/user")
    public User getUser(@RequestParam("phoneNo") String phoneNo){
         return userService.getUserByPhoneNo(phoneNo);
    }
}
