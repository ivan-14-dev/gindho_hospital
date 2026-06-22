package com.gindho.hr.model;

import com.gindho.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "presences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Presence extends BaseEntity {
    private Long employeId;
    private LocalDateTime datePresence;
    private String type;
}
