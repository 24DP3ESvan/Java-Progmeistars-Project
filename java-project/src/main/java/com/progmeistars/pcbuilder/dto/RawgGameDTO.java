package com.progmeistars.pcbuilder.dto;

import java.util.List;
import java.util.Map;

public class RawgGameDTO {
    private Long id;
    private String name;
    private String released;
    private Double rating;
    private Integer metacritic;
    private String backgroundImage;
    private List<Map<String, Object>> platforms;

    public RawgGameDTO() {
    }

    public RawgGameDTO(Long id,
                       String name,
                       String released,
                       Double rating,
                       Integer metacritic,
                       String backgroundImage,
                       List<Map<String, Object>> platforms) {
        this.id = id;
        this.name = name;
        this.released = released;
        this.rating = rating;
        this.metacritic = metacritic;
        this.backgroundImage = backgroundImage;
        this.platforms = platforms;
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

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getMetacritic() {
        return metacritic;
    }

    public void setMetacritic(Integer metacritic) {
        this.metacritic = metacritic;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public List<Map<String, Object>> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<Map<String, Object>> platforms) {
        this.platforms = platforms;
    }
}
