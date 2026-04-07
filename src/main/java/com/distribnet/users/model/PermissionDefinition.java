package com.distribnet.users.model;

import com.distribnet.common.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "permission_definition")
public class PermissionDefinition extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "module_name", nullable = false, length = 100)
    private String moduleName;

    @Column(length = 500)
    private String description;
}
