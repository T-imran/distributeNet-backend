package com.distribnet.salesmen.service;

import com.distribnet.common.model.Tenant;
import com.distribnet.dealers.repository.DealerRepository;
import com.distribnet.salesmen.dto.SalesmanDto;
import com.distribnet.salesmen.model.Salesman;
import com.distribnet.salesmen.repository.SalesmanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SalesmanService {

    private final SalesmanRepository salesmanRepository;

    public List<SalesmanDto> findAllForTenant(Tenant tenant) {
        return salesmanRepository.findByTenant(tenant).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public SalesmanDto findById(Tenant tenant, UUID id) {
        return salesmanRepository.findByTenantAndId(tenant, id)
                .map(this::toDto)
                .orElse(null);
    }

    @Transactional
    public SalesmanDto create(Tenant tenant, SalesmanDto dto) {
        Salesman salesman = new Salesman();
        salesman.setTenant(tenant);
        salesman.setRegion(dto.getRegion());
        salesman.setDivision(dto.getDivision());
        salesman.setDistrict(dto.getDistrict());
        salesman.setMonthlyTarget(dto.getMonthlyTarget());
        salesman.setMonthlyAchieved(dto.getMonthlyAchieved());
        salesman.setAchievementPct(dto.getAchievementPct());
        salesman.setVisitTarget(dto.getVisitTarget());
        salesman.setVisitsCompleted(dto.getVisitsCompleted());
        salesman.setOrdersThisMonth(dto.getOrdersThisMonth());
        salesman.setCollectionThisMonth(dto.getCollectionThisMonth());
        salesman.setAttendanceRate(dto.getAttendanceRate());
        salesman.setCurrentStatus(Salesman.SalesmanStatus.valueOf(dto.getCurrentStatus().toUpperCase()));
        return toDto(salesmanRepository.save(salesman));
    }

    @Transactional
    public SalesmanDto update(Tenant tenant, UUID id, SalesmanDto dto) {
        return salesmanRepository.findByTenantAndId(tenant, id).map(existing -> {
            existing.setRegion(dto.getRegion());
            existing.setDivision(dto.getDivision());
            existing.setDistrict(dto.getDistrict());
            existing.setMonthlyTarget(dto.getMonthlyTarget());
            existing.setMonthlyAchieved(dto.getMonthlyAchieved());
            existing.setAchievementPct(dto.getAchievementPct());
            existing.setVisitTarget(dto.getVisitTarget());
            existing.setVisitsCompleted(dto.getVisitsCompleted());
            existing.setOrdersThisMonth(dto.getOrdersThisMonth());
            existing.setCollectionThisMonth(dto.getCollectionThisMonth());
            existing.setAttendanceRate(dto.getAttendanceRate());
            existing.setCurrentStatus(Salesman.SalesmanStatus.valueOf(dto.getCurrentStatus().toUpperCase()));
            return toDto(salesmanRepository.save(existing));
        }).orElse(null);
    }

    @Transactional
    public boolean remove(Tenant tenant, UUID id) {
        return salesmanRepository.findByTenantAndId(tenant, id).map(existing -> {
            salesmanRepository.delete(existing);
            return true;
        }).orElse(false);
    }

    private SalesmanDto toDto(Salesman salesman) {
        SalesmanDto dto = new SalesmanDto();
        dto.setId(salesman.getId().toString());
        dto.setRegion(salesman.getRegion());
        dto.setDivision(salesman.getDivision());
        dto.setDistrict(salesman.getDistrict());
        if (salesman.getManager() != null) {
            dto.setManagerId(salesman.getManager().getId().toString());
        }
        dto.setMonthlyTarget(salesman.getMonthlyTarget());
        dto.setMonthlyAchieved(salesman.getMonthlyAchieved());
        dto.setAchievementPct(salesman.getAchievementPct());
        dto.setVisitTarget(salesman.getVisitTarget());
        dto.setVisitsCompleted(salesman.getVisitsCompleted());
        dto.setOrdersThisMonth(salesman.getOrdersThisMonth());
        dto.setCollectionThisMonth(salesman.getCollectionThisMonth());
        dto.setAttendanceRate(salesman.getAttendanceRate());
        dto.setCurrentStatus(salesman.getCurrentStatus().name());
        return dto;
    }
}
