package com.progmeistars.pcbuilder;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.progmeistars.pcbuilder.dto.RawgGameDTO;
import com.progmeistars.pcbuilder.dto.RawgGameSearchResponse;
import com.progmeistars.pcbuilder.service.RawgService;

@ExtendWith(MockitoExtension.class)
class RawgServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private RawgService rawgService;

    @BeforeEach
    void setUp() {
        rawgService = new RawgService(restTemplate, "test-key", "https://api.rawg.io/api");
    }

    @Test
    void searchGamesReturnsEmptyWhenResponseIsNull() {
        when(restTemplate.getForObject(any(URI.class), eq(RawgGameSearchResponse.class))).thenReturn(null);

        List<RawgGameDTO> results = rawgService.searchGames("doom", 10);

        assertThat(results).isEmpty();
    }

    @Test
    void searchGamesThrowsWhenRawgServiceFails() {
        when(restTemplate.getForObject(any(URI.class), eq(RawgGameSearchResponse.class)))
                .thenThrow(new RestClientException("Service unavailable"));

        assertThatThrownBy(() -> rawgService.searchGames("doom", 10))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("RAWG service unavailable");
    }

    @Test
    void getGameReturnsNullWhenNotFound() {
        when(restTemplate.getForObject(any(URI.class), eq(RawgGameDTO.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        RawgGameDTO game = rawgService.getGame("123");

        assertThat(game).isNull();
    }
}
