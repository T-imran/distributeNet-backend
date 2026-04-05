package com.distribnet.payments.service;

import com.distribnet.common.model.Tenant;
import com.distribnet.payments.dto.PaymentDto;
import com.distribnet.payments.model.Payment;
import com.distribnet.payments.repository.PaymentRepository;
import com.distribnet.retailers.repository.RetailerRepository;
import com.distribnet.dealers.repository.DealerRepository;
import com.distribnet.salesmen.repository.SalesmanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RetailerRepository retailerRepository;
    private final DealerRepository dealerRepository;
    private final SalesmanRepository salesmanRepository;

    public List<PaymentDto> findAllForTenant(Tenant tenant) {
        return paymentRepository.findByTenant(tenant).stream().map(this::toDto).collect(Collectors.toList());
    }

    public PaymentDto findById(Tenant tenant, UUID id) {
        return paymentRepository.findByTenantAndId(tenant, id).map(this::toDto).orElse(null);
    }

    @Transactional
    public PaymentDto create(Tenant tenant, PaymentDto dto) {
        Payment payment = new Payment();
        payment.setTenant(tenant);
        payment.setPaymentNumber(dto.getPaymentNumber());
        payment.setRetailer(retailerRepository.findById(UUID.fromString(dto.getRetailerId())).orElse(null));
        payment.setDealer(dto.getDealerId() != null ? dealerRepository.findById(UUID.fromString(dto.getDealerId())).orElse(null) : null);
        payment.setSalesman(dto.getSalesmanId() != null ? salesmanRepository.findById(UUID.fromString(dto.getSalesmanId())).orElse(null) : null);
        payment.setAmount(dto.getAmount());
        payment.setMethod(dto.getMethod());
        payment.setStatus(Payment.PaymentStatus.valueOf(dto.getStatus().toUpperCase()));
        payment.setReceivedAt(dto.getReceivedAt());
        return toDto(paymentRepository.save(payment));
    }

    @Transactional
    public PaymentDto verify(Tenant tenant, UUID id) {
        return paymentRepository.findByTenantAndId(tenant, id).map(payment -> {
            payment.setStatus(Payment.PaymentStatus.VERIFIED);
            payment.setVerifiedAt(LocalDateTime.now());
            return toDto(paymentRepository.save(payment));
        }).orElse(null);
    }

    private PaymentDto toDto(Payment payment) {
        PaymentDto dto = new PaymentDto();
        dto.setId(payment.getId().toString());
        dto.setPaymentNumber(payment.getPaymentNumber());
        dto.setRetailerId(payment.getRetailer() != null ? payment.getRetailer().getId().toString() : null);
        dto.setDealerId(payment.getDealer() != null ? payment.getDealer().getId().toString() : null);
        dto.setSalesmanId(payment.getSalesman() != null ? payment.getSalesman().getId().toString() : null);
        dto.setAmount(payment.getAmount());
        dto.setMethod(payment.getMethod());
        dto.setStatus(payment.getStatus().name());
        dto.setReceivedAt(payment.getReceivedAt());
        dto.setVerifiedAt(payment.getVerifiedAt());
        dto.setVerifiedBy(payment.getVerifiedBy() != null ? payment.getVerifiedBy().getId().toString() : null);
        return dto;
    }
}
