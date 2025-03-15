package com.website.military.domain.dto.test.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class CheckListResponse {
    private List<CheckResponse> correctList;
    private List<CheckResponse> incorrectList;
}
