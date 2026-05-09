package com.progmeistars.pcbuilder.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.progmeistars.pcbuilder.dto.PartDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PartService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PartService.class);
    private static final Map<String, String> CATEGORY_FILES = Map.of(
            "Korpuss", "case.json",
            "CPU", "cpu.json",
            "Draivers", "internal-hard-drive.json",
            "Operativa atmiņa", "memory.json",
            "Mātesplate", "motherboard.json",
            "Videokarte", "video-card.json"
    );

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String partsPath;
    private final Map<String, List<PartDTO>> cache = new HashMap<>();

    public PartService(@Value("${pcbuilder.parts.path:../parts}") String partsPath) {
        this.partsPath = partsPath;
    }

    @PostConstruct
    public void init() {
        CATEGORY_FILES.forEach((category, filename) -> {
            try {
                cache.put(category, loadParts(category, filename));
            } catch (IOException e) {
                throw new IllegalStateException("Unable to load parts from " + filename, e);
            }
        });
    }

    private List<PartDTO> loadParts(String category, String filename) throws IOException {
        Path jsonFile = Paths.get(partsPath).resolve(filename);
        if (Files.exists(jsonFile)) {
            return readPartsFromPath(category, jsonFile);
        }

        try (InputStream resourceStream = getClass().getClassLoader().getResourceAsStream("parts/" + filename)) {
            if (resourceStream != null) {
                return readPartsFromStream(category, resourceStream);
            }
        }

        LOGGER.warn("Parts file not found: {} and no classpath fallback resource found", jsonFile.toAbsolutePath());
        return Collections.emptyList();
    }

    private List<PartDTO> readPartsFromPath(String category, Path jsonFile) throws IOException {
        List<Map<String, Object>> values = objectMapper.readValue(Files.readAllBytes(jsonFile), new TypeReference<>() {});
        return buildPartList(category, values);
    }

    private List<PartDTO> readPartsFromStream(String category, InputStream stream) throws IOException {
        List<Map<String, Object>> values = objectMapper.readValue(stream, new TypeReference<>() {});
        return buildPartList(category, values);
    }

    private List<PartDTO> buildPartList(String category, List<Map<String, Object>> values) {
        List<PartDTO> result = new ArrayList<>();
        for (Map<String, Object> raw : values) {
            String name = Optional.ofNullable(raw.get("name")).map(Object::toString).orElse("Unknown");
            Double price = Optional.ofNullable(raw.get("price"))
                    .map(value -> {
                        if (value instanceof Number) {
                            return ((Number) value).doubleValue();
                        }
                        try {
                            return Double.parseDouble(value.toString());
                        } catch (Exception ex) {
                            return 0.0;
                        }
                    })
                    .orElse(0.0);
            Map<String, Object> attributes = new HashMap<>(raw);
            attributes.remove("name");
            attributes.remove("price");
            result.add(PartDTO.builder()
                    .category(category)
                    .name(name)
                    .price(price)
                    .attributes(attributes)
                    .build());
        }
        return result;
    }

    public List<PartDTO> searchParts(String category, String text, Double maxPrice, String sort) {
        List<PartDTO> parts = cache.getOrDefault(category, List.of());

        if (text != null && !text.isBlank()) {
            String lower = text.toLowerCase(Locale.ROOT);
            parts = parts.stream()
                    .filter(part -> part.getName().toLowerCase(Locale.ROOT).contains(lower)
                            || part.getAttributes().values().stream().anyMatch(value -> value != null && value.toString().toLowerCase(Locale.ROOT).contains(lower)))
                    .collect(Collectors.toList());
        }

        if (maxPrice != null) {
            parts = parts.stream()
                    .filter(part -> part.getPrice() != null && part.getPrice() <= maxPrice)
                    .collect(Collectors.toList());
        }

        if (sort != null) {
            switch (sort) {
                case "price":
                    parts = parts.stream().sorted(Comparator.comparing(part -> Optional.ofNullable(part.getPrice()).orElse(0.0))).collect(Collectors.toList());
                    break;
                case "priceDesc":
                    parts = parts.stream().sorted(Comparator.comparing((PartDTO part) -> Optional.ofNullable(part.getPrice()).orElse(0.0)).reversed()).collect(Collectors.toList());
                    break;
                case "name":
                    parts = parts.stream().sorted(Comparator.comparing(PartDTO::getName, String.CASE_INSENSITIVE_ORDER)).collect(Collectors.toList());
                    break;
                default:
                    break;
            }
        }

        return parts;
    }

    public Optional<PartDTO> findPart(String category, String name) {
        return cache.getOrDefault(category, List.of()).stream()
                .filter(part -> part.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public List<String> getCategories() {
        return new ArrayList<>(CATEGORY_FILES.keySet());
    }
}
