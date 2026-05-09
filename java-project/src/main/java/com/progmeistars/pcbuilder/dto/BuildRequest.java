package com.progmeistars.pcbuilder.dto;

import java.util.HashMap;
import java.util.Map;

public class BuildRequest {
    private String name;
    private Map<String, PartDTO> components;

    public BuildRequest() {
        this.components = new HashMap<>();
    }

    public BuildRequest(String name, Map<String, PartDTO> components) {
        this.name = name;
        this.components = components != null ? new HashMap<>(components) : new HashMap<>();
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

    public static BuildRequestBuilder builder() {
        return new BuildRequestBuilder();
    }

    public static class BuildRequestBuilder {
        private String name;
        private Map<String, PartDTO> components = new HashMap<>();

        public BuildRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BuildRequestBuilder components(Map<String, PartDTO> components) {
            this.components = components != null ? new HashMap<>(components) : new HashMap<>();
            return this;
        }

        public BuildRequest build() {
            return new BuildRequest(name, components);
        }
    }
}
