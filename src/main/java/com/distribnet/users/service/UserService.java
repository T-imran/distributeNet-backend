package com.distribnet.users.service;

import com.distribnet.common.model.Tenant;
import com.distribnet.users.model.ManagedUser;
import com.distribnet.users.repository.ManagedUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final ManagedUserRepository managedUserRepository;

    public List<ManagedUser> findAllForTenant(Tenant tenant) {
        return managedUserRepository.findByTenant(tenant);
    }

    public ManagedUser findById(Tenant tenant, UUID id) {
        return managedUserRepository.findById(id).filter(user -> user.getTenant().getId().equals(tenant.getId())).orElse(null);
    }

    @Transactional
    public ManagedUser save(ManagedUser user) {
        return managedUserRepository.save(user);
    }
}
