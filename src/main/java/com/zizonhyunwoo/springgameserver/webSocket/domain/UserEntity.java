package com.zizonhyunwoo.springgameserver.webSocket.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class UserEntity extends DateType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id",columnDefinition = "uuid")
    private UUID id;
    @Column(nullable = false)
    private String password;
    @Column(unique = true, nullable = false)
    private String email;
    @Column(unique = true, nullable = false)
    private String nickname;

}