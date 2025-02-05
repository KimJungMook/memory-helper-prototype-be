package com.website.military.domain.dto.wordsets.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeleteResponseDto {
    private Long id;
    private String setName;
}
