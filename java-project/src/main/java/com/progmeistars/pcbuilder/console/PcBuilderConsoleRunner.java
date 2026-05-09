package com.progmeistars.pcbuilder.console;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

import com.progmeistars.pcbuilder.dto.BuildRequest;
import com.progmeistars.pcbuilder.dto.PartDTO;
import com.progmeistars.pcbuilder.service.BuildService;
import com.progmeistars.pcbuilder.service.PartService;

@Component
public class PcBuilderConsoleRunner implements ApplicationRunner {

    private final PartService partService;
    private final BuildService buildService;
    private final ConfigurableApplicationContext context;

    public PcBuilderConsoleRunner(PartService partService, BuildService buildService, ConfigurableApplicationContext context) {
        this.partService = partService;
        this.buildService = buildService;
        this.context = context;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!args.containsOption("console")) {
            return;
        }

        Scanner scanner = new Scanner(System.in);
        println("=== PC Builder Console režīms ===");
        println("Izmanto šo režīmu, lai atrastu detaļas un izveidotu savu PC build.");
        println("Lai pārtrauktu, ieraksti 'exit'.\n");

        while (true) {
            println("Izvēlies darbību:");
            println("1) Parādīt detaļu kategorijas");
            println("2) Meklēt detaļas kategorijā");
            println("3) Skatīt detaļas informāciju");
            println("4) Izveidot jaunu PC build");
            println("5) Rādīt saglabātos build");
            println("0) Iziet");
            print("Tava izvēle: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit") || input.equals("0")) {
                println("Programma pārtraukta.");
                break;
            }

            switch (input) {
                case "1" -> showCategories();
                case "2" -> searchParts(scanner);
                case "3" -> showPartDetails(scanner);
                case "4" -> createBuild(scanner);
                case "5" -> listBuilds();
                default -> println("Nederīga izvēle. Lūdzu izvēlies 0-5.");
            }
            println("\n---\n");
        }

        context.close();
    }

    private void showCategories() {
        List<String> categories = partService.getCategories();
        println("Pieejamās kategorijas:");
        categories.forEach(category -> println("- " + category));
    }

    private void searchParts(Scanner scanner) {
        println("Ievadi kategoriju:");
        String category = scanner.nextLine().trim();
        if (category.isBlank()) {
            println("Kategorija nevar būt tukša.");
            return;
        }

        println("Meklēšanas teksts (var atstāt tukšu):");
        String text = scanner.nextLine().trim();
        if (text.isBlank()) {
            text = null;
        }

        println("Maksimālā cena (var atstāt tukšu):");
        String maxPriceInput = scanner.nextLine().trim();
        Double maxPrice = null;
        if (!maxPriceInput.isBlank()) {
            try {
                maxPrice = Double.parseDouble(maxPriceInput);
            } catch (NumberFormatException e) {
                println("Nederīga cena. Tiks izmantota nav ierobežojuma.");
            }
        }

        println("Kārtot pēc (name, price, priceDesc) [name]:");
        String sort = scanner.nextLine().trim();
        if (sort.isBlank()) {
            sort = "name";
        }

        List<PartDTO> parts = partService.searchParts(category, text, maxPrice, sort);
        if (parts.isEmpty()) {
            println("Nav atrasts neviens detaļa.");
            return;
        }

        println("Atrastas detaļas:");
        for (int i = 0; i < parts.size(); i++) {
            PartDTO part = parts.get(i);
            println((i + 1) + ") " + part.getName() + " — " + formatPrice(part.getPrice()));
        }
    }

    private void showPartDetails(Scanner scanner) {
        println("Ievadi kategoriju:");
        String category = scanner.nextLine().trim();
        if (category.isBlank()) {
            println("Kategorija nevar būt tukša.");
            return;
        }

        println("Ievadi detaļas nosaukumu:");
        String name = scanner.nextLine().trim();
        if (name.isBlank()) {
            println("Nosaukums nevar būt tukšs.");
            return;
        }

        Optional<PartDTO> partOpt = partService.findPart(category, name);
        if (partOpt.isEmpty()) {
            println("Detaļa nav atrasta.");
            return;
        }

        PartDTO part = partOpt.get();
        println("Detaļas informācija:");
        println("- Kategorija: " + part.getCategory());
        println("- Nosaukums: " + part.getName());
        println("- Cena: " + formatPrice(part.getPrice()));
        println("- Atribūti:");
        part.getAttributes().forEach((key, value) -> println("    " + key + ": " + value));
    }

    private void createBuild(Scanner scanner) {
        println("Build nosaukums:");
        String name = scanner.nextLine().trim();
        if (name.isBlank()) {
            println("Build nosaukums nevar būt tukšs.");
            return;
        }

        Map<String, PartDTO> components = new LinkedHashMap<>();
        for (String category : partService.getCategories()) {
            println("Izvēlies detaļu kategorijai '" + category + "' (ievadi nosaukumu vai atstāj tukšu, lai izlaistu):");
            String selectedName = scanner.nextLine().trim();
            if (selectedName.isBlank()) {
                continue;
            }
            Optional<PartDTO> partOpt = partService.findPart(category, selectedName);
            if (partOpt.isEmpty()) {
                println("Detaļa '" + selectedName + "' kategorijā '" + category + "' nav atrasta. Tiks izlaista.");
                continue;
            }
            components.put(category, partOpt.get());
        }

        if (components.isEmpty()) {
            println("Build jābūt vismaz ar vienu komponenti.");
            return;
        }

        BuildRequest request = BuildRequest.builder()
                .name(name)
                .components(components)
                .build();

        try {
            var result = buildService.saveBuild(request);
            println("Build saglabāts ar ID " + result.getId() + ".");
            println("Build: " + result.getName());
            result.getComponents().forEach((category, part) -> println("- " + category + ": " + part.getName() + " (" + formatPrice(part.getPrice()) + ")"));
        } catch (IllegalArgumentException e) {
            println("Neizdevās saglabāt build: " + e.getMessage());
        }
    }

    private void listBuilds() {
        var builds = buildService.listBuilds();
        if (builds.isEmpty()) {
            println("Nav saglabātu build.");
            return;
        }

        println("Saglabātie build:");
        builds.forEach(build -> {
            println("ID " + build.getId() + " — " + build.getName());
            build.getComponents().forEach((category, part) -> println("    " + category + ": " + part.getName() + " — " + formatPrice(part.getPrice())));
        });
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
