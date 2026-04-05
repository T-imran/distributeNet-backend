package com.distribnet.retailers.service;

import com.distribnet.common.model.Tenant;
import com.distribnet.dealers.model.Dealer;
import com.distribnet.dealers.repository.DealerRepository;
import com.distribnet.retailers.dto.RetailerDto;
import com.distribnet.retailers.model.Retailer;
import com.distribnet.retailers.repository.RetailerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RetailerService {

    private final RetailerRepository retailerRepository;
    private final DealerRepository dealerRepository;

    public List<RetailerDto> findAllForTenant(Tenant tenant) {
        return retailerRepository.findByTenant(tenant).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public RetailerDto findById(Tenant tenant, UUID id) {
        return retailerRepository.findByTenantAndId(tenant, id)
                .map(this::toDto)
                .orElse(null);
    }

    @Transactional
    public RetailerDto create(Tenant tenant, RetailerDto dto) {
        Retailer retailer = new Retailer();
        retailer.setTenant(tenant);
        retailer.setDealer(dto.getDealerId() != null ? dealerRepository.findById(UUID.fromString(dto.getDealerId())).orElse(null) : null);
        retailer.setCode(dto.getCode());
        retailer.setName(dto.getName());
        retailer.setOwnerName(dto.getOwnerName());
        retailer.setPhone(dto.getPhone());
        retailer.setEmail(dto.getEmail());
        retailer.setAddress(dto.getAddress());
        retailer.setTier(dto.getTier());
        retailer.setBusinessType(dto.getBusinessType());
        retailer.setOutstandingBalance(dto.getOutstandingBalance());
        retailer.setTotalPurchases(dto.getTotalPurchases());
        retailer.setVisitCount(dto.getVisitCount());
        retailer.setOrderCount(dto.getOrderCount());
        retailer.setRegion(dto.getRegion());
        retailer.setGps(dto.getGps());
        retailer.setStatus(Retailer.RetailerStatus.valueOf(dto.getStatus().toUpperCase()));
        return toDto(retailerRepository.save(retailer));
    }

    @Transactional
    public RetailerDto update(Tenant tenant, UUID id, RetailerDto dto) {
        return retailerRepository.findByTenantAndId(tenant, id).map(existing -> {
            existing.setDealer(dto.getDealerId() != null ? dealerRepository.findById(UUID.fromString(dto.getDealerId())).orElse(null) : null);
            existing.setName(dto.getName());
            existing.setOwnerName(dto.getOwnerName());
            existing.setPhone(dto.getPhone());
            existing.setEmail(dto.getEmail());
            existing.setAddress(dto.getAddress());
            existing.setTier(dto.getTier());
            existing.setBusinessType(dto.getBusinessType());
            existing.setOutstandingBalance(dto.getOutstandingBalance());
            existing.setTotalPurchases(dto.getTotalPurchases());
            existing.setVisitCount(dto.getVisitCount());
            existing.setOrderCount(dto.getOrderCount());
            existing.setRegion(dto.getRegion());
            existing.setGps(dto.getGps());
            existing.setStatus(Retailer.RetailerStatus.valueOf(dto.getStatus().toUpperCase()));
            return toDto(retailerRepository.save(existing));
        }).orElse(null);
    }

    @Transactional
    public boolean delete(Tenant tenant, UUID id) {
        return retailerRepository.findByTenantAndId(tenant, id).map(existing -> {
            retailerRepository.delete(existing);
            return true;
        }).orElse(false);
    }

    private RetailerDto toDto(Retailer retailer) {
        RetailerDto dto = new RetailerDto();
        dto.setId(retailer.getId().toString());
        dto.setCode(retailer.getCode());
        dto.setName(retailer.getName());
        dto.setOwnerName(retailer.getOwnerName());
        dto.setPhone(retailer.getPhone());
        dto.setEmail(retailer.getEmail());
        dto.setAddress(retailer.getAddress());
        dto.setTier(retailer.getTier());
        dto.setBusinessType(retailer.getBusinessType());
        dto.setOutstandingBalance(retailer.getOutstandingBalance());
        dto.setTotalPurchases(retailer.getTotalPurchases());
        dto.setVisitCount(retailer.getVisitCount());
        dto.setOrderCount(retailer.getOrderCount());
        dto.setRegion(retailer.getRegion());
        dto.setGps(retailer.getGps());
        dto.setStatus(retailer.getStatus().name());
        if (retailer.getDealer() != null) {
            dto.setDealerId(retailer.getDealer().getId().toString());
        }
        return dto;
    }
}
