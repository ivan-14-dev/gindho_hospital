package com.gindho.base;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApiResponse<T> {
    private boolean success; private String message; private T data;
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @SuppressWarnings("unchecked")
    public static <T> ApiResponse<T> ok(T d) {
        Class<?> clazz = d != null ? d.getClass() : Object.class;
        return (ApiResponse<T>) ApiResponse.builder().success(true).message("Success").data(d).build();
    }
    
    @SuppressWarnings("unchecked")
    public static <T> ApiResponse<T> ok(String m, T d) {
        return (ApiResponse<T>) ApiResponse.builder().success(true).message(m).data(d).build();
    }
    
    @SuppressWarnings("unchecked")
    public static <T> ApiResponse<T> error(String m) {
        return (ApiResponse<T>) ApiResponse.builder().success(false).message(m).build();
    }
    
    // Helper: explicitly typed ok() for Lists to avoid inference issues
    public static <T> ApiResponse<List<T>> okList(List<T> list) {
        return new ApiResponse<>(true, "Success", list, LocalDateTime.now());
    }
    
    // Helper: explicitly typed ok() for a single entity
    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(true, "Success", data, LocalDateTime.now());
    }
}
