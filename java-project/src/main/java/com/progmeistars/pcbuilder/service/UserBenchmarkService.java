package com.progmeistars.pcbuilder.service;

import com.progmeistars.pcbuilder.dto.PartDTO;
import com.progmeistars.pcbuilder.dto.UserBenchmarkPartDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserBenchmarkService {

    private final RestTemplate restTemplate;
    private final PartService partService;
    private final String searchUrl;

    public UserBenchmarkService(RestTemplate restTemplate,
                                PartService partService,
                                @Value("${userbenchmark.search.url:https://www.userbenchmark.com/Search?search=}") String searchUrl) {
        this.restTemplate = restTemplate;
        this.partService = partService;
        this.searchUrl = searchUrl;
    }

    public List<UserBenchmarkPartDTO> searchBenchmarkParts(String query) {
        String url = searchUrl + URLEncoder.encode(query, StandardCharsets.UTF_8);
        try {
            String html = restTemplate.getForObject(url, String.class);
            if (html != null) {
                Document doc = Jsoup.parse(html);
                List<UserBenchmarkPartDTO> results = new ArrayList<>();
                for (Element link : doc.select("a[href]")) {
                    String text = link.text();
                    String href = link.attr("href");
                    if (text.isBlank() || href.isBlank()) {
                        continue;
                    }
                    if (href.contains("/cpu/") || href.contains("/gpu/")) {
                        results.add(UserBenchmarkPartDTO.builder()
                                .name(text)
                                .category(href.contains("cpu") ? "CPU" : "Videokarte")
                                .detailsUrl("https://www.userbenchmark.com" + href)
                                .score(null)
                                .build());
                    }
                }
                if (!results.isEmpty()) {
                    return results;
                }
            }
        } catch (RestClientException ex) {
            // ignore and fallback
        }

        return buildFallbackParts(query);
    }

    private List<UserBenchmarkPartDTO> buildFallbackParts(String query) {
        List<UserBenchmarkPartDTO> response = new ArrayList<>();
        for (String category : partService.getCategories()) {
            for (PartDTO part : partService.searchParts(category, query, null, "name")) {
                response.add(UserBenchmarkPartDTO.builder()
                        .name(part.getName())
                        .category(part.getCategory())
                        .score(part.getPrice() != null ? Math.max(1.0, 1000.0 / (part.getPrice() + 1.0)) : null)
                        .detailsUrl(null)
                        .build());
            }
        }
        return response;
    }
}
