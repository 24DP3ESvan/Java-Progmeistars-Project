package com.progmeistars.pcbuilder.service;

import com.progmeistars.pcbuilder.dto.BuildDTO;
import com.progmeistars.pcbuilder.dto.BuildRequest;
import com.progmeistars.pcbuilder.entity.BuildEntity;
import com.progmeistars.pcbuilder.exception.ResourceNotFoundException;
import com.progmeistars.pcbuilder.repository.BuildRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BuildService {

    private final BuildRepository repository;

    public BuildService(BuildRepository repository) {
        this.repository = repository;
    }

    public BuildDTO saveBuild(BuildRequest request) {
        if (request == null || request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Build name cannot be empty");
        }

        Map<String, ?> components = request.getComponents();
        if (components == null || components.isEmpty()) {
            throw new IllegalArgumentException("Build must contain at least one component");
        }

        BuildEntity entity = BuildEntity.builder()
                .name(request.getName().trim())
                .components(mapComponents(request.getComponents()))
                .build();

        return toDto(repository.save(entity));
    }

    public BuildDTO getBuild(Long id) {
        return repository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Build not found: " + id));
    }

    public java.util.List<BuildDTO> listBuilds() {
        return repository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public BuildDTO updateBuild(Long id, BuildRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Build request cannot be null");
        }

        BuildEntity entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Build not found: " + id));
        if (request.getName() != null && !request.getName().isBlank()) {
            entity.setName(request.getName().trim());
        }
        if (request.getComponents() != null && !request.getComponents().isEmpty()) {
            entity.setComponents(mapComponents(request.getComponents()));
        }
        return toDto(entity);
    }

    private Map<String, com.progmeistars.pcbuilder.dto.PartDTO> mapComponents(Map<String, com.progmeistars.pcbuilder.dto.PartDTO> components) {
        return components.entrySet().stream()
                .filter(entry -> entry.getKey() != null && entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private BuildDTO toDto(BuildEntity entity) {
        return BuildDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .components(entity.getComponents())
                .build();
    }
}
