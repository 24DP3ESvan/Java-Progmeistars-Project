package com.progmeistars.pcbuilder.console;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.progmeistars.pcbuilder.dto.BuildDTO;
import com.progmeistars.pcbuilder.dto.BuildRequest;
import com.progmeistars.pcbuilder.dto.PartDTO;
import com.progmeistars.pcbuilder.dto.RawgGameDTO;
import com.progmeistars.pcbuilder.service.BuildService;
import com.progmeistars.pcbuilder.service.PartService;
import com.progmeistars.pcbuilder.service.RawgService;

@Component
public class PcBuilderConsoleRunner implements ApplicationRunner {

    private final PartService partService;
    private final BuildService buildService;
    private final RawgService rawgService;
    private final ConfigurableApplicationContext context;
    private final Map<String, PartDTO> currentComponents = new LinkedHashMap<>();
    private String currentBuildName = "";

    public PcBuilderConsoleRunner(
            PartService partService,
            BuildService buildService,
            RawgService rawgService,
            ConfigurableApplicationContext context) {
        this.partService = partService;
        this.buildService = buildService;
        this.rawgService = rawgService;
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!args.containsOption("console")) {
            return;
        }

        Scanner scanner = new Scanner(System.in);
        println("=== PC Builder Console rezims ===");
        println("Izmanto so rezimu, lai atrastu detalas un izveidotu savu PC build.");
        println("Lai partrauktu, ieraksti 'exit'.\n");

        while (true) {
            println("Izvelies darbibu:");
            println("1) Paradit detalju kategorijas");
            println("2) Meklet detalas kategorija");
            println("3) Parbaudit pievienotas komponentes");
            println("4) Parbaudit build statusu");
            println("5) Saglabat build");
            println("6) Ieladet saglabatu build");
            println("0) Izet");
            print("Tava izvele: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit") || input.equals("0")) {
                println("Programma partraukta.");
                break;
            }

            switch (input) {
                case "1" -> showCategories();
                case "2" -> searchParts(scanner);
                case "3" -> checkComponent(scanner);
                case "4" -> checkBuild(scanner);
                case "5" -> saveBuild(scanner);
                case "6" -> insertSavedBuild(scanner);
                default -> println("Nederiga izvele. Ludzu izvelies 0-6.");
            }
            println("\n---\n");
        }

        context.close();
    }

    private void showCategories() {
        List<String> categories = partService.getCategories();
        println("Peejamas kategorijas:");
        categories.forEach(category -> println("- " + category));
    }

    private void searchParts(Scanner scanner) {
        println("Ievadi kategoriju:");
        String categoryInput = scanner.nextLine().trim();
        if (categoryInput.isBlank()) {
            println("Kategorija nevar but tuksa.");
            return;
        }

        String category = resolveCategory(categoryInput);
        if (category == null) {
            println("Kategorija nav atrasta. Ludzu ievadi pareizu kategoriju.");
            return;
        }

        println("Meklesanas teksts (var atstat tuksu):");
        String text = scanner.nextLine().trim();
        if (text.isBlank()) {
            text = null;
        }

        println("Maksimala cena (var atstat tuksu):");
        String maxPriceInput = scanner.nextLine().trim();
        Double maxPrice = null;
        if (!maxPriceInput.isBlank()) {
            try {
                maxPrice = Double.parseDouble(maxPriceInput);
            } catch (NumberFormatException e) {
                println("Nederiga cena. Tiks izmantota nav ierobezojuma.");
            }
        }

        println("Kartot pec (name, price, priceDesc) [name]:");
        String sort = scanner.nextLine().trim();
        if (sort.isBlank()) {
            sort = "name";
        }

        List<PartDTO> parts = partService.searchParts(category, text, maxPrice, sort);
        if (parts.isEmpty()) {
            println("Nav atrasts neviens detalas.");
            return;
        }

        println("Atrastas detalas:");
        for (int i = 0; i < parts.size(); i++) {
            PartDTO part = parts.get(i);
            println((i + 1) + ") " + part.getName() + " - " + formatPrice(part.getPrice()));
        }

        print("Izvele (ievadi numuru, lai pievienotu build vai atstaj tuksu, lai atcelt): ");
        String choice = scanner.nextLine().trim();
        if (choice.isBlank()) {
            return;
        }

        try {
            int selected = Integer.parseInt(choice);
            if (selected < 1 || selected > parts.size()) {
                println("Nederigs numurs.");
                return;
            }
            PartDTO part = parts.get(selected - 1);
            addComponentToBuild(part);
            println(selected + " was added to the build");
        } catch (NumberFormatException e) {
            println("Nederigs ievade. Ievadi tikai skaitli.");
        }
    }

    private void checkComponent(Scanner scanner) {
        if (currentComponents.isEmpty()) {
            println("Nav nevienas pievienotas komponentes.");
            return;
        }

        println("Pievienotas komponentes:");
        List<String> categories = currentComponents.keySet().stream().toList();
        for (int i = 0; i < categories.size(); i++) {
            String category = categories.get(i);
            println((i + 1) + ") " + category + " - " + currentComponents.get(category).getName());
        }

        print("Ievadi numuru, lai parbauditu detalas informaciju, vai atstaj tuksu, lai atcelt: ");
        String selection = scanner.nextLine().trim();
        if (selection.isBlank()) {
            return;
        }

        try {
            int index = Integer.parseInt(selection);
            if (index < 1 || index > categories.size()) {
                println("Nederigs numurs.");
                return;
            }
            String category = categories.get(index - 1);
            PartDTO part = currentComponents.get(category);
            println("Detaljas informacija:");
            println("- Kategorija: " + category);
            println("- Nosaukums: " + part.getName());
            println("- Cena: " + formatPrice(part.getPrice()));
            println("- Atributi:");
            part.getAttributes().forEach((key, value) -> println("    " + key + ": " + value));
        } catch (NumberFormatException e) {
            println("Nederigs ievade. Ievadi tikai skaitli.");
        }
    }

    private void checkBuild(Scanner scanner) {
        println("Build status:");
        if (currentBuildName.isBlank()) {
            println("Build nosaukums nav iestatits.");
        } else {
            println("Build nosaukums: " + currentBuildName);
        }

        if (currentComponents.isEmpty()) {
            println("Nav pievienotas komponentes.");
        } else {
            println("Pievienotas komponentes:");
            currentComponents.forEach((category, part) -> println("- " + category + ": " + part.getName() + " (" + formatPrice(part.getPrice()) + ")"));
        }

        List<String> missing = getMissingCategories();
        if (!missing.isEmpty()) {
            println("Trukst sekojosas kategorijas:");
            missing.forEach(category -> println("- " + category));
            println("Lai pievienotu trukstosas komponentes, izmanto 2. punktu.");
            return;
        }

        println("Build ir pilns. Informacija no RAWG spelem:");
        String query = deriveBuildQuery();
        try {
            List<RawgGameDTO> games = rawgService.searchGames(query, 6);
            if (games.isEmpty()) {
                println("Nav atrasts neviens spele RAWG.");
                return;
            }
            for (int i = 0; i < games.size(); i++) {
                RawgGameDTO game = games.get(i);
                double fps = estimateFps(game);
                println((i + 1) + ") " + game.getName() + " - aptuvenie FPS: " + String.format(Locale.ROOT, "%.0f", fps));
            }
        } catch (Exception e) {
            String message = e.getMessage();
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                message += " (" + e.getCause().getMessage() + ")";
            }
            println("Klauda sazinoties ar RAWG: " + message);
        }
    }

    private void insertSavedBuild(Scanner scanner) {
        List<BuildDTO> builds = buildService.listBuilds();
        if (builds.isEmpty()) {
            println("Nav saglabatu build.");
            return;
        }

        println("Saglabatie build:");
        for (int i = 0; i < builds.size(); i++) {
            BuildDTO build = builds.get(i);
            println((i + 1) + ") " + build.getName() + " (ID: " + build.getId() + ")");
        }

        print("Ievadi numuru, lai ieladet build, vai atstaj tuksu, lai atcelt: ");
        String selection = scanner.nextLine().trim();
        if (selection.isBlank()) {
            return;
        }

        try {
            int index = Integer.parseInt(selection);
            if (index < 1 || index > builds.size()) {
                println("Nederigs numurs.");
                return;
            }
            BuildDTO build = builds.get(index - 1);
            currentComponents.clear();
            currentComponents.putAll(build.getComponents());
            currentBuildName = build.getName();
            println("Build '" + build.getName() + "' ieladets un komponenetes atjaunotas.");
        } catch (NumberFormatException e) {
            println("Nederigs ievade. Ievadi tikai skaitli.");
        }
    }

    private void saveBuild(Scanner scanner) {
        if (currentBuildName.isBlank()) {
            print("Ievadi build nosaukumu: ");
            currentBuildName = scanner.nextLine().trim();
            if (currentBuildName.isBlank()) {
                currentBuildName = "Build " + System.currentTimeMillis();
            }
        }

        if (currentComponents.isEmpty()) {
            println("Lai saglabatu build, pievieno vismaz vienu komponentu.");
            return;
        }

        BuildRequest request = BuildRequest.builder()
                .name(currentBuildName)
                .components(currentComponents)
                .build();

        try {
            BuildDTO saved = buildService.saveBuild(request);
            println("Build saglabats ar ID " + saved.getId() + ".");
        } catch (IllegalArgumentException e) {
            println("Neizdevas saglabat build: " + e.getMessage());
        }
    }

    private List<String> getMissingCategories() {
        return partService.getCategories().stream()
                .filter(category -> !currentComponents.containsKey(category))
                .toList();
    }

    private void addComponentToBuild(PartDTO part) {
        if (currentBuildName.isBlank()) {
            currentBuildName = "Current Build";
        }
        currentComponents.put(part.getCategory(), part);
    }

    private double estimateFps(RawgGameDTO game) {
        if (game.getMetacritic() != null) {
            return game.getMetacritic() * 2.0;
        }
        if (game.getRating() != null) {
            return game.getRating() * 20.0;
        }
        return 60.0;
    }

    private String deriveBuildQuery() {
        if (currentComponents.containsKey("Videokarte")) {
            return currentComponents.get("Videokarte").getName() + " benchmark";
        }
        if (currentComponents.containsKey("CPU")) {
            return currentComponents.get("CPU").getName() + " benchmark";
        }
        if (!currentBuildName.isBlank() && !currentBuildName.equals("Current Build")) {
            return currentBuildName;
        }
        return "pc benchmark";
    }

    private String resolveCategory(String categoryInput) {
        return partService.getCategories().stream()
                .filter(category -> category.equalsIgnoreCase(categoryInput))
                .findFirst()
                .orElse(null);
    }

    private String formatPrice(Double price) {
        return price == null ? "0.00" : String.format(Locale.ROOT, "%.2f EUR", price);
    }

    private void println(String text) {
        System.out.println(text);
    }

    private void print(String text) {
        System.out.print(text);
    }
}
