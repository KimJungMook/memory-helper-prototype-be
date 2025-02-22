package com.website.military.domain.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDto {
    @Schema(description = "이름 입력", example = "정묵")
    private String username;
    @Schema(description = "이메일 입력", example = "wjdanr@naver.com")
    private String email;
    @Schema(description = "비밀번호 입력", example = "wjdanr")
    private String password;
}
