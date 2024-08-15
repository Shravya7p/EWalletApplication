package com.userService.model;

import com.userService.enums.UserIdentificationType;
import com.userService.enums.UserStatus;
import com.userService.enums.UserType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level= AccessLevel.PRIVATE)
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @Column(length = 30)
    String name;

    @Column(length = 30,unique = true)
    String email;

    @Column(unique = true,nullable = false,length = 40)
    String phoneNo;

    String password;

    String authorities;

    @Enumerated(value=EnumType.STRING)
    UserType userType;

    @Enumerated(value = EnumType.STRING)
    UserStatus userStatus;

    @Enumerated(value=EnumType.STRING)
    UserIdentificationType userIdentificationType;

    String userIdentificationValue;

    @CreationTimestamp
    Date createdOn;

    @UpdateTimestamp
    Date updatedOn;


    @Override
    public String getUsername() {
        return phoneNo;
    }

    public Collection<? extends GrantedAuthority> getAuthorities(){
        return Arrays.stream(authorities.split(","))
                .map(authority->new SimpleGrantedAuthority(authority))
                .collect(Collectors.toList());
    }
}
