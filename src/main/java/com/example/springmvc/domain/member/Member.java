package com.example.springmvc.domain.member;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.io.Serializable;

/**
 * 회원 도메인
 */
@Data
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    @NotEmpty
    private String loginId;   // 로그인 ID

    @NotEmpty
    private String name;      // 사용자 이름

    @NotEmpty
    private String password;
}
