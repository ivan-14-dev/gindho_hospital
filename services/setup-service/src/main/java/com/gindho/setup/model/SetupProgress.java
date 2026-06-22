package com.gindho.setup.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "setup_progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetupProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String stepName;

    @Column(nullable = false)
    private boolean completed = false;

    @Column
    private String status;

    @Column
    private String message;

    @Column
    private Long completedAt;
}