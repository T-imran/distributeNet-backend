package com.distribnet.approvals.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerApprovalRequestCreateDto {

    @NotBlank
    @Size(max = 100)
    private String requestType;

    @NotBlank
    @Size(max = 255)
    private String customerName;

    @Email
    @Size(max = 255)
    private String customerEmail;

    @Size(max = 20)
    private String customerPhone;

    @NotBlank
    @Size(max = 255)
    private String summary;

    @NotBlank
    @Size(max = 2000)
    private String details;
}
