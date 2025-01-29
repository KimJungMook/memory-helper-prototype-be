package com.website.military.domain.dto.auth.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetUserInfoFromUsernameResponseDto {
    private String username;
    private String email;
}
