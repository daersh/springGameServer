package com.zizonhyunwoo.springgameserver.user.vo;

import com.zizonhyunwoo.springgameserver.user.domain.UserEntity;
import lombok.Data;

import java.time.LocalDateTime;

public class UserInfo {

    @Data
    public static class Info{
        public String nickname;
        public String email;
        public LocalDateTime createdAt;
        public LocalDateTime updatedAt;
        public Info(UserEntity user){
            this.nickname = user.getNickname();
            this.email = user.getEmail();
            this.createdAt = user.getCreatedAt();
            this.updatedAt = user.getUpdatedAt();
        }
    }
}
