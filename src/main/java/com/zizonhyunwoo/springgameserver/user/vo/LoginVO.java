package com.zizonhyunwoo.springgameserver.user.vo;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public class LoginVO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Login{
        public String email;
        public String password;
    }
}
