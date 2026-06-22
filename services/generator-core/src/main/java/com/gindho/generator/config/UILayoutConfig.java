package com.gindho.generator.config;
import lombok.*; import java.util.List;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UILayoutConfig {
    private String appName;
    private String version;
    private String theme;
    private List<UISectionDefinition> sections;
    private List<UIMenuDefinition> menus;
}
