package com.distribnet.dealers.service;

import com.distribnet.common.model.Tenant;
import com.distribnet.common.model.User;
import com.distribnet.dealers.dto.DealerDto;
import com.distribnet.dealers.model.Dealer;
import com.distribnet.dealers.repository.DealerRepository;
import com.distribnet.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DealerService {

    private final DealerRepository dealerRepository;
    private final UserRepository userRepository;

    public List<DealerDto> findAllForTenant(Tenant tenant) {
        return dealerRepository.findByTenant(tenant).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public DealerDto findById(Tenant tenant, UUID id) {
        return dealerRepository.findByTenantAndId(tenant, id)
                .map(this::toDto)
                .orElse(null);
    }

    @Transactional
    public DealerDto create(Tenant tenant, DealerDto dto) {
        Dealer dealer = new Dealer();
        dealer.setTenant(tenant);
        dealer.setCode(dto.getCode());
        dealer.setName(dto.getName());
        dealer.setOwnerName(dto.getOwnerName());
        dealer.setEmail(dto.getEmail());
        dealer.setPhone(dto.getPhone());
        dealer.setStatus(Dealer.DealerStatus.valueOf(dto.getStatus().toUpperCase()));
        dealer.setDivision(dto.getDivision());
        dealer.setDistrict(dto.getDistrict());
        dealer.setThana(dto.getThana());
        dealer.setTerritory(dto.getTerritory());
        dealer.setCreditLimit(dto.getCreditLimit());
        dealer.setOutstandingBalance(dto.getOutstandingBalance());
        dealer.setTotalSales(dto.getTotalSales());
        dealer.setJoinedAt(dto.getJoinedAt());
        return toDto(dealerRepository.save(dealer));
    }

    @Transactional
    public DealerDto update(Tenant tenant, UUID id, DealerDto dto) {
        return dealerRepository.findByTenantAndId(tenant, id)
                .map(existing -> {
                    existing.setName(dto.getName());
                    existing.setOwnerName(dto.getOwnerName());
                    existing.setEmail(dto.getEmail());
                    existing.setPhone(dto.getPhone());
                    existing.setStatus(Dealer.DealerStatus.valueOf(dto.getStatus().toUpperCase()));
                    existing.setDivision(dto.getDivision());
                    existing.setDistrict(dto.getDistrict());
                    existing.setThana(dto.getThana());
                    existing.setTerritory(dto.getTerritory());
                    existing.setCreditLimit(dto.getCreditLimit());
                    existing.setOutstandingBalance(dto.getOutstandingBalance());
                    existing.setTotalSales(dto.getTotalSales());
                    existing.setJoinedAt(dto.getJoinedAt());
                    return toDto(dealerRepository.save(existing));
                })
                .orElse(null);
    }

    @Transactional
    public boolean deactivate(Tenant tenant, UUID id) {
        return dealerRepository.findByTenantAndId(tenant, id)
                .map(existing -> {
                    existing.setStatus(Dealer.DealerStatus.INACTIVE);
                    dealerRepository.save(existing);
                    return true;
                }).orElse(false);
    }

    private DealerDto toDto(Dealer dealer) {
        DealerDto dto = new DealerDto();
        dto.setId(dealer.getId().toString());
        dto.setCode(dealer.getCode());
        dto.setName(dealer.getName());
        dto.setOwnerName(dealer.getOwnerName());
        dto.setEmail(dealer.getEmail());
        dto.setPhone(dealer.getPhone());
        dto.setStatus(dealer.getStatus().name());
        dto.setDivision(dealer.getDivision());
        dto.setDistrict(dealer.getDistrict());
        dto.setThana(dealer.getThana());
        dto.setTerritory(dealer.getTerritory());
        dto.setCreditLimit(dealer.getCreditLimit());
        dto.setOutstandingBalance(dealer.getOutstandingBalance());
        dto.setTotalSales(dealer.getTotalSales());
        dto.setJoinedAt(dealer.getJoinedAt());
        return dto;
    }
}
