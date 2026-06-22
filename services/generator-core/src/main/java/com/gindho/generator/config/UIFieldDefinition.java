package com.gindho.generator.config;
import lombok.*; import java.util.List; import java.util.Map;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UIFieldDefinition {
    private String id; private String label; private String icon;
    private String type; private String title; private String subtitle;
    private String api; private String apiMethod; private String path;
    private String sectionId; private String format; private String placeholder;
    private String defaultValue; private String optionsApi; private String dependsOn;
    private String name; private String action; private String style;
    private String permission; private String confirmMessage; private String successMessage;
    private String errorMessage; private String openIn; private String theme;
    private int order; private int width; private int refreshInterval;
    private boolean required; private boolean sortable; private boolean filterable;
    private List<String> roles; private List<String> permissions; private List<String> options;
    private List<UISectionDefinition> children; private List<UIWidgetDefinition> widgets;
    private List<UIColumnDefinition> columns; private List<UIFieldDefinition> fields;
    private List<UIActionDefinition> actions; private List<UIMenuDefinition> menus;
    private List<UIBadgeConfig> badges;
    private Map<String, Object> validation; private Map<String, Object> visibleWhen;
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class UIBadgeConfig {
        private String field; private String value; private String color; private String label;
    }
}
