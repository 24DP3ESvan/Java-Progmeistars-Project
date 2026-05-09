package com.progmeistars.pcbuilder.service;

import com.progmeistars.pcbuilder.dto.RawgGameDTO;
import com.progmeistars.pcbuilder.dto.RawgGameSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

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
        try {
            URI uri = new URI(String.format("%s/games?search=%s&page_size=%d&key=%s", apiUrl, encode(query), pageSize, apiKey));
            RawgGameSearchResponse response = restTemplate.getForObject(uri, RawgGameSearchResponse.class);
            return response != null && response.getResults() != null ? response.getResults() : Collections.emptyList();
        } catch (RestClientException | URISyntaxException e) {
            return Collections.emptyList();
        }
    }

    public RawgGameDTO getGame(String id) {
        try {
            URI uri = new URI(String.format("%s/games/%s?key=%s", apiUrl, id, apiKey));
            return restTemplate.getForObject(uri, RawgGameDTO.class);
        } catch (RestClientException | URISyntaxException e) {
            return null;
        }
    }

    private String encode(String value) {
        if (value == null) {
            return "";
        }
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
