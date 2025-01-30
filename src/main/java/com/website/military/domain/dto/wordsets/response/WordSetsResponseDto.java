package com.website.military.domain.dto.wordsets.response;


import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WordSetsResponseDto {
    private Long setId;
    private String setName;
    private Date createdAt;
}
