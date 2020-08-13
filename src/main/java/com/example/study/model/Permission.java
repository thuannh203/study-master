package com.example.study.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "permission")
public class Permission {

    @Id
    @JsonProperty("permission_name")
    @Column(name = "permission_name")
    @NotNull
    private String permissionName;
}
