package com.progmeistars.pcbuilder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.progmeistars.pcbuilder.dto.PartDTO;
import com.progmeistars.pcbuilder.dto.UserBenchmarkPartDTO;
import com.progmeistars.pcbuilder.service.PartService;
import com.progmeistars.pcbuilder.service.UserBenchmarkService;

@ExtendWith(MockitoExtension.class)
class UserBenchmarkServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private PartService partService;

    @InjectMocks
    private UserBenchmarkService benchmarkService;

    @BeforeEach
    void setUp() {
        benchmarkService = new UserBenchmarkService(restTemplate, partService, "https://www.userbenchmark.com/Search?search=");
    }

    @Test
    void searchBenchmarkPartsReturnsLinksFromHtml() {
        String html = "<html><body>"
                + "<a href=\"/cpu/example-cpu\">Example CPU</a>"
                + "<a href=\"/gpu/example-gpu\">Example GPU</a>"
                + "</body></html>";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(html);

        List<UserBenchmarkPartDTO> result = benchmarkService.searchBenchmarkParts("test");

        assertThat(result).hasSize(2);
        assertThat(result).extracting(UserBenchmarkPartDTO::getCategory).containsExactlyInAnyOrder("CPU", "Videokarte");
    }

    @Test
    void searchBenchmarkPartsFallsBackWhenRemoteCallFails() {
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenThrow(new RestClientException("timeout"));
        when(partService.getCategories()).thenReturn(List.of("CPU"));
        when(partService.searchParts("CPU", "Ryzen", null, "name")).thenReturn(List.of(
                PartDTO.builder().category("CPU").name("AMD Ryzen 9").price(499.0).build()
        ));

        List<UserBenchmarkPartDTO> result = benchmarkService.searchBenchmarkParts("Ryzen");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("AMD Ryzen 9");
        assertThat(result.get(0).getDetailsUrl()).isNull();
    }
}
