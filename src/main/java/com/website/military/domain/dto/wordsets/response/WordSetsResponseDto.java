package com.website.military.domain.dto.wordsets.response;

import java.sql.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WordSetsResponseDto {
    private String setName;
    private Date createdAt;
}
