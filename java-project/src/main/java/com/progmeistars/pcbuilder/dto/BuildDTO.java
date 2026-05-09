package com.progmeistars.pcbuilder.dto;

import java.util.HashMap;
import java.util.Map;

public class BuildDTO {
    private Long id;
    private String name;
    private Map<String, PartDTO> components;

    public BuildDTO() {
        this.components = new HashMap<>();
    }

    public BuildDTO(Long id, String name, Map<String, PartDTO> components) {
        this.id = id;
        this.name = name;
        this.components = components != null ? new HashMap<>(components) : new HashMap<>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, PartDTO> getComponents() {
        return components;
    }

    public void setComponents(Map<String, PartDTO> components) {
        this.components = components != null ? new HashMap<>(components) : new HashMap<>();
    }

    public static BuildDTOBuilder builder() {
        return new BuildDTOBuilder();
    }

    public static class BuildDTOBuilder {
        private Long id;
        private String name;
        private Map<String, PartDTO> components = new HashMap<>();

        public BuildDTOBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BuildDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BuildDTOBuilder components(Map<String, PartDTO> components) {
            this.components = components != null ? new HashMap<>(components) : new HashMap<>();
            return this;
        }

        public BuildDTO build() {
            return new BuildDTO(id, name, components);
        }
    }
}
