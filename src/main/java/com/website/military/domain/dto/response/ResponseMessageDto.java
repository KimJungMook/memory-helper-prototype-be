package com.website.military.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "set")
public class ResponseMessageDto {
    private String code;
    private String message;
}
