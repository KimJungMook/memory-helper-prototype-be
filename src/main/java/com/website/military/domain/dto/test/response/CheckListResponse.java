package com.website.military.domain.dto.test.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class CheckListResponse {
    private Long resultId;
    private List<CheckResponse> correctList;
    private List<CheckResponse> incorrectList;
}
