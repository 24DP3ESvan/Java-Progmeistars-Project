package com.progmeistars.pcbuilder.dto;

import java.util.HashMap;
import java.util.Map;

public class PartDTO {
    private String category;
    private String name;
    private Double price;
    private Map<String, Object> attributes;

    public PartDTO() {
        this.attributes = new HashMap<>();
    }

    public PartDTO(String category, String name, Double price, Map<String, Object> attributes) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.attributes = attributes != null ? new HashMap<>(attributes) : new HashMap<>();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes != null ? new HashMap<>(attributes) : new HashMap<>();
    }

    public static PartDTOBuilder builder() {
        return new PartDTOBuilder();
    }

    public static class PartDTOBuilder {
        private String category;
        private String name;
        private Double price;
        private Map<String, Object> attributes = new HashMap<>();

        public PartDTOBuilder category(String category) {
            this.category = category;
            return this;
        }

        public PartDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public PartDTOBuilder price(Double price) {
            this.price = price;
            return this;
        }

        public PartDTOBuilder attributes(Map<String, Object> attributes) {
            this.attributes = attributes != null ? new HashMap<>(attributes) : new HashMap<>();
            return this;
        }

        public PartDTO build() {
            return new PartDTO(category, name, price, attributes);
        }
    }
}
