package com.distribnet.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiValidationError {
    private String code;
    private String field;
    private String message;
}
