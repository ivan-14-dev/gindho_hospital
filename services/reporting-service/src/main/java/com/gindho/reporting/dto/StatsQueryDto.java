package com.gindho.reporting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatsQueryDto {
    private String metric;
    private java.time.LocalDate from;
    private java.time.LocalDate to;
}
