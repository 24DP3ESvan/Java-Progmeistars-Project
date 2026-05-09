package com.progmeistars.pcbuilder;

import com.progmeistars.pcbuilder.dto.BuildDTO;
import com.progmeistars.pcbuilder.dto.BuildRequest;
import com.progmeistars.pcbuilder.dto.PartDTO;
import com.progmeistars.pcbuilder.service.BuildService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BuildServiceTest {

    @Autowired
    private BuildService buildService;

    @Test
    void saveAndRetrieveBuild() {
        PartDTO cpu = PartDTO.builder().category("CPU").name("Example CPU").price(199.99).build();
        PartDTO gpu = PartDTO.builder().category("Videokarte").name("Example GPU").price(329.99).build();

        BuildRequest request = BuildRequest.builder()
                .name("Test Build")
                .components(Map.of("CPU", cpu, "Videokarte", gpu))
                .build();

        BuildDTO saved = buildService.saveBuild(request);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Test Build");
        assertThat(saved.getComponents()).containsKeys("CPU", "Videokarte");

        BuildDTO loaded = buildService.getBuild(saved.getId());
        assertThat(loaded.getName()).isEqualTo("Test Build");
        assertThat(loaded.getComponents()).containsKey("CPU");
    }
}
