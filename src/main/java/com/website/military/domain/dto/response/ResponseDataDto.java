package com.website.military.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "set")
public class ResponseDataDto<D> {
    private String code;
    private D data;
}
