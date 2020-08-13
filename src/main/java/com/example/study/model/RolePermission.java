package com.example.study.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "role_permission")
public class RolePermission {

    @Id
    @JsonIgnore
    @Column(name = "role_permission_id")
    @NotNull
    private String rolePermissionId;

    @JsonProperty("role_name")
    @Column(name = "role_name")
    @NotNull
    private String roleName;

    @JsonProperty("permission_name")
    @Column(name = "permission_name")
    @NotNull
    private String permissionName;
}
