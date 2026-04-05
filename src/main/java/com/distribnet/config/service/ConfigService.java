package com.distribnet.config.service;

import com.distribnet.common.model.Tenant;
import com.distribnet.common.repository.TenantRepository;
import com.distribnet.config.model.TenantConfig;
import com.distribnet.config.repository.TenantConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConfigService {

    private final TenantConfigRepository configRepository;
    private final TenantRepository tenantRepository;

    public TenantConfig getConfig(Tenant tenant) {
        return configRepository.findByTenant(tenant).orElseGet(() -> {
            Tenant managedTenant = tenantRepository.findById(tenant.getId()).orElse(tenant);
            TenantConfig config = new TenantConfig();
            config.setTenant(managedTenant);
            return configRepository.save(config);
        });
    }

    @Transactional
    public TenantConfig saveConfig(Tenant tenant, TenantConfig config) {
        TenantConfig existing = configRepository.findByTenant(tenant).orElseGet(() -> {
            TenantConfig created = new TenantConfig();
            created.setTenant(tenantRepository.findById(tenant.getId()).orElse(tenant));
            return created;
        });
        existing.setModules(config.getModules());
        existing.setRegions(config.getRegions());
        existing.setBranding(config.getBranding());
        existing.setWorkflows(config.getWorkflows());
        existing.setGeoLocation(config.getGeoLocation());
        existing.setCustomFields(config.getCustomFields());
        return configRepository.save(existing);
    }
}
