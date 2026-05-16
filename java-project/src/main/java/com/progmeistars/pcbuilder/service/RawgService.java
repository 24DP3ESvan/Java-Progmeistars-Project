package com.progmeistars.pcbuilder.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.progmeistars.pcbuilder.dto.RawgGameDTO;
import com.progmeistars.pcbuilder.dto.RawgGameSearchResponse;

@Service
public class RawgService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String apiUrl;

    public RawgService(RestTemplate restTemplate,
                       @Value("${rawg.api.key:}") String apiKey,
                       @Value("${rawg.api.url:https://api.rawg.io/api}") String apiUrl) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    public List<RawgGameDTO> searchGames(String query, int pageSize) {
        if (!hasApiKey()) {
            return fallbackSearchGames(query, pageSize);
        }

        try {
            String uriString = String.format("%s/games?search=%s&page_size=%d&key=%s", apiUrl, encode(query), pageSize, encode(apiKey));
            URI uri = new URI(uriString);
            RawgGameSearchResponse response = restTemplate.getForObject(uri, RawgGameSearchResponse.class);
            return response != null && response.getResults() != null ? response.getResults() : Collections.emptyList();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid RAWG query URI", e);
        } catch (RestClientException e) {
            return fallbackSearchGames(query, pageSize);
        }
    }

    private List<RawgGameDTO> fallbackSearchGames(String query, int pageSize) {
        String normalized = query == null ? "" : query.toLowerCase();
        if (normalized.contains("radeon") || normalized.contains("rx") || normalized.contains("amd")) {
            return List.of(
                    new RawgGameDTO(1L, "Cyberpunk 2077", "2020-12-10", 4.5, 90, null, Collections.emptyList()),
                    new RawgGameDTO(2L, "Doom Eternal", "2020-03-20", 4.8, 92, null, Collections.emptyList()),
                    new RawgGameDTO(3L, "Forza Horizon 5", "2021-11-09", 4.6, 92, null, Collections.emptyList()),
                    new RawgGameDTO(4L, "Elden Ring", "2022-02-25", 4.8, 97, null, Collections.emptyList()),
                    new RawgGameDTO(5L, "Metro Exodus", "2019-02-15", 4.3, 85, null, Collections.emptyList()),
                    new RawgGameDTO(6L, "Horizon Zero Dawn", "2017-03-01", 4.7, 89, null, Collections.emptyList())
            ).subList(0, Math.min(pageSize, 6));
        }
        if (normalized.contains("geforce") || normalized.contains("rtx") || normalized.contains("nvidia")) {
            return List.of(
                    new RawgGameDTO(7L, "Control", "2019-08-27", 4.3, 85, null, Collections.emptyList()),
                    new RawgGameDTO(8L, "Shadow of the Tomb Raider", "2018-09-14", 4.1, 83, null, Collections.emptyList()),
                    new RawgGameDTO(9L, "Red Dead Redemption 2", "2018-10-26", 4.8, 97, null, Collections.emptyList()),
                    new RawgGameDTO(10L, "The Witcher 3: Wild Hunt", "2015-05-19", 4.9, 93, null, Collections.emptyList()),
                    new RawgGameDTO(11L, "Assassin's Creed Valhalla", "2020-11-10", 4.2, 80, null, Collections.emptyList()),
                    new RawgGameDTO(12L, "Horizon Forbidden West", "2022-02-18", 4.6, 88, null, Collections.emptyList())
            ).subList(0, Math.min(pageSize, 6));
        }
        return List.of(
                new RawgGameDTO(13L, "Cyberpunk 2077", "2020-12-10", 4.5, 90, null, Collections.emptyList()),
                new RawgGameDTO(14L, "Doom Eternal", "2020-03-20", 4.8, 92, null, Collections.emptyList()),
                new RawgGameDTO(15L, "Forza Horizon 5", "2021-11-09", 4.6, 92, null, Collections.emptyList()),
                new RawgGameDTO(16L, "Red Dead Redemption 2", "2018-10-26", 4.8, 97, null, Collections.emptyList()),
                new RawgGameDTO(17L, "Control", "2019-08-27", 4.3, 85, null, Collections.emptyList()),
                new RawgGameDTO(18L, "The Witcher 3: Wild Hunt", "2015-05-19", 4.9, 93, null, Collections.emptyList())
        ).subList(0, Math.min(pageSize, 6));
    }

    public RawgGameDTO getGame(String id) {
        if (!hasApiKey()) {
            throw new IllegalStateException("RAWG API key is not configured. Add rawg.api.key to application.properties.");
        }

        try {
            String uriString = String.format("%s/games/%s?key=%s", apiUrl, id, encode(apiKey));
            URI uri = new URI(uriString);
            return restTemplate.getForObject(uri, RawgGameDTO.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw new IllegalStateException("RAWG service unavailable: " + e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Invalid RAWG query URI", e);
        } catch (RestClientException e) {
            throw new IllegalStateException("RAWG service unavailable: " + e.getMessage(), e);
        }
    }

    private boolean hasApiKey() {
        return apiKey != null && !apiKey.isBlank();
    }

    private String encode(String value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
