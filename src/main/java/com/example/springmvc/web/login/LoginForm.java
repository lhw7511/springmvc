package com.example.springmvc.web.login;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 로그인 폼 전송 객체
 */
@Data
public class LoginForm {

    @NotEmpty
    private String loginId;

    @NotEmpty
    private String password;
}
