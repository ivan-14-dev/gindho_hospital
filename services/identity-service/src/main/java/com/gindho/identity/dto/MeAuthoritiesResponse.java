package com.gindho.identity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MeAuthoritiesResponse {
    private Long userId;
    private String email;
    private String role;
    private List<String> authorities;
}
