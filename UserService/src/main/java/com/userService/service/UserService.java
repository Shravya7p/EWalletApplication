package com.userService.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.userService.dto.CreateUserRequest;
import com.userService.enums.UserType;
import com.userService.mapper.UserMapper;
import com.userService.model.User;
import com.userService.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.userService.constants.KafkaConstants.USER_CREATION_TOPIC;
import static com.userService.constants.UserCreationTopicConstants.*;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    KafkaTemplate<String,String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public User loadUserByUsername(String phoneNo) throws UsernameNotFoundException {
        User user =  userRepository.findByPhoneNo(phoneNo);
        if(user == null){
            throw new UsernameNotFoundException("Username does not exist");
        }
        return user;
    }

    public User createUser(CreateUserRequest userRequest) {
       User user = UserMapper.mapToUser(userRequest);
       user.setUserType(UserType.USER);
       user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
       user.setAuthorities("USER");
       log.info("user created : {} ",user);
       userRepository.save(user);

       log.info("user saved : {}",user);
        //publish the data to kafka

        //notification service requires username, email
        //wallet service requires phoneNo, userStatus

        ObjectNode objectNode = objectMapper.createObjectNode();
        objectNode.put(EMAIL,user.getEmail());
        objectNode.put(NAME,user.getName());
        objectNode.put(PHONENO,user.getPhoneNo());
        objectNode.put(USERID,user.getId());

        String kafkaMessage=objectNode.toString();
        kafkaTemplate.send(USER_CREATION_TOPIC,kafkaMessage);

        log.info("Published message to kafka: {}",kafkaMessage);
        return user;

    }

    public User getUserByPhoneNo(String phoneNo) {
        return userRepository.findByPhoneNo(phoneNo);
    }
}
