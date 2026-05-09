package com.progmeistars.pcbuilder.entity;

import com.progmeistars.pcbuilder.converter.PartMapAttributeConverter;
import com.progmeistars.pcbuilder.dto.PartDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "builds")
public class BuildEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(name = "components_json", nullable = false)
    @Convert(converter = PartMapAttributeConverter.class)
    private Map<String, PartDTO> components;

    public BuildEntity() {
        this.components = new HashMap<>();
    }

    public BuildEntity(Long id, String name, Map<String, PartDTO> components) {
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

    public static BuildEntityBuilder builder() {
        return new BuildEntityBuilder();
    }

    public static class BuildEntityBuilder {
        private Long id;
        private String name;
        private Map<String, PartDTO> components = new HashMap<>();

        public BuildEntityBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public BuildEntityBuilder name(String name) {
            this.name = name;
            return this;
        }

        public BuildEntityBuilder components(Map<String, PartDTO> components) {
            this.components = components != null ? new HashMap<>(components) : new HashMap<>();
            return this;
        }

        public BuildEntity build() {
            return new BuildEntity(id, name, components);
        }
    }
}
