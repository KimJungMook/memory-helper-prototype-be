package com.website.military.domain.dto.wordsets.response;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetWordsBySetIdFinalResponse {
    private String name;
    private List<GetWordsBySetIdResponse> list; 
}
