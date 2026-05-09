package com.progmeistars.pcbuilder.controller;

import com.progmeistars.pcbuilder.dto.BuildDTO;
import com.progmeistars.pcbuilder.dto.BuildRequest;
import com.progmeistars.pcbuilder.dto.PartDTO;
import com.progmeistars.pcbuilder.dto.RawgGameDTO;
import com.progmeistars.pcbuilder.dto.UserBenchmarkPartDTO;
import com.progmeistars.pcbuilder.service.BuildService;
import com.progmeistars.pcbuilder.service.PartService;
import com.progmeistars.pcbuilder.service.RawgService;
import com.progmeistars.pcbuilder.service.UserBenchmarkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PcBuilderController {

    private final PartService partService;
    private final BuildService buildService;
    private final RawgService rawgService;
    private final UserBenchmarkService userBenchmarkService;

    public PcBuilderController(
            PartService partService,
            BuildService buildService,
            RawgService rawgService,
            UserBenchmarkService userBenchmarkService) {
        this.partService = partService;
        this.buildService = buildService;
        this.rawgService = rawgService;
        this.userBenchmarkService = userBenchmarkService;
    }

    @GetMapping("/parts")
    public List<String> listCategories() {
        return partService.getCategories();
    }

    @GetMapping("/parts/{category}")
    public List<PartDTO> searchParts(
            @PathVariable String category,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false, defaultValue = "name") String sort
    ) {
        return partService.searchParts(category, search, maxPrice, sort);
    }

    @GetMapping("/parts/{category}/detail")
    public PartDTO getPartDetails(
            @PathVariable String category,
            @RequestParam String name
    ) {
        return partService.findPart(category, name)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Part not found"));
    }

    @GetMapping("/games/search")
    public List<RawgGameDTO> searchGames(
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "10") int pageSize
    ) {
        return rawgService.searchGames(query, pageSize);
    }

    @GetMapping("/games/{id}")
    public RawgGameDTO getGame(@PathVariable String id) {
        RawgGameDTO game = rawgService.getGame(id);
        if (game == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        }
        return game;
    }

    @GetMapping("/benchmarks/search")
    public List<UserBenchmarkPartDTO> searchBenchmarks(@RequestParam String query) {
        return userBenchmarkService.searchBenchmarkParts(query);
    }

    @PostMapping("/builds")
    public ResponseEntity<BuildDTO> createBuild(@RequestBody BuildRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(buildService.saveBuild(request));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/builds")
    public List<BuildDTO> listBuilds() {
        return buildService.listBuilds();
    }

    @GetMapping("/builds/{id}")
    public BuildDTO getBuild(@PathVariable Long id) {
        try {
            return buildService.getBuild(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @PutMapping("/builds/{id}")
    public BuildDTO updateBuild(@PathVariable Long id, @RequestBody BuildRequest request) {
        try {
            return buildService.updateBuild(id, request);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
