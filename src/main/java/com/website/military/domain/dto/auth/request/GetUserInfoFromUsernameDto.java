package com.website.military.domain.dto.auth.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserInfoFromUsernameDto {
    @Schema(description = "유저의 이름", example = "정묵")
    private String username;
}
