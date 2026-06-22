package com.gindho.hr.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresenceDto {
    private Long id;
    private Long employeId;
    private java.time.LocalDateTime datePresence;
    private String type;
}
