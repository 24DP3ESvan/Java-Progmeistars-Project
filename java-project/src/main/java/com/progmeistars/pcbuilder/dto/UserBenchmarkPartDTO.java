package com.progmeistars.pcbuilder.dto;

public class UserBenchmarkPartDTO {
    private String name;
    private String category;
    private Double score;
    private String detailsUrl;

    public UserBenchmarkPartDTO() {
    }

    public UserBenchmarkPartDTO(String name, String category, Double score, String detailsUrl) {
        this.name = name;
        this.category = category;
        this.score = score;
        this.detailsUrl = detailsUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getDetailsUrl() {
        return detailsUrl;
    }

    public void setDetailsUrl(String detailsUrl) {
        this.detailsUrl = detailsUrl;
    }

    public static UserBenchmarkPartDTOBuilder builder() {
        return new UserBenchmarkPartDTOBuilder();
    }

    public static class UserBenchmarkPartDTOBuilder {
        private String name;
        private String category;
        private Double score;
        private String detailsUrl;

        public UserBenchmarkPartDTOBuilder name(String name) {
            this.name = name;
            return this;
        }

        public UserBenchmarkPartDTOBuilder category(String category) {
            this.category = category;
            return this;
        }

        public UserBenchmarkPartDTOBuilder score(Double score) {
            this.score = score;
            return this;
        }

        public UserBenchmarkPartDTOBuilder detailsUrl(String detailsUrl) {
            this.detailsUrl = detailsUrl;
            return this;
        }

        public UserBenchmarkPartDTO build() {
            return new UserBenchmarkPartDTO(name, category, score, detailsUrl);
        }
    }
}
