package com.website.military.domain.dto.wordsets.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterResponseDto {
    private Long setId;
    private String setName;
}
