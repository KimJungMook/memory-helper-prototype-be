package com.website.military.domain.dto.wordsets.response;

import java.time.Instant;
import java.util.List;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetWordsBySetIdFinalResponse {
    private String name;
    private List<GetWordsBySetIdResponse> list; 
    private Instant createdAt;
    private List<Long> examIds;
}
