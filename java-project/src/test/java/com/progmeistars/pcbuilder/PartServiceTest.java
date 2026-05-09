package com.progmeistars.pcbuilder;

import com.progmeistars.pcbuilder.dto.PartDTO;
import com.progmeistars.pcbuilder.service.PartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PartServiceTest {

    @Autowired
    private PartService partService;

    @Test
    void searchPartsReturnsResultsForCpuQuery() {
        List<PartDTO> results = partService.searchParts("CPU", "Ryzen", 300.0, "price");

        assertThat(results).isNotEmpty();
        assertThat(results.get(0).getPrice()).isLessThanOrEqualTo(300.0);
        assertThat(results.stream().anyMatch(part -> part.getName().toLowerCase().contains("ryzen"))).isTrue();
    }

    @Test
    void getPartDetailsReturnsExactCpu() {
        PartDTO part = partService.findPart("CPU", "AMD Ryzen 7 7800X3D").orElse(null);

        assertThat(part).isNotNull();
        assertThat(part.getCategory()).isEqualTo("CPU");
        assertThat(part.getName()).contains("7800X3D");
    }
}
