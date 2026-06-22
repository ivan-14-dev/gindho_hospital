package com.gindho.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "role_templates", uniqueConstraints = {
        @UniqueConstraint(name = "role_templates_name_uk", columnNames = "name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleTemplate extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoleTemplatePermission> permissions = new ArrayList<>();
}
