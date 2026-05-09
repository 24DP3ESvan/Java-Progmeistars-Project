package com.progmeistars.pcbuilder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PcBuilderApplication {
    public static void main(String[] args) {
        boolean consoleMode = false;
        for (String arg : args) {
            if ("--console".equals(arg)) {
                consoleMode = true;
                break;
            }
        }

        if (consoleMode) {
            SpringApplication application = new SpringApplication(PcBuilderApplication.class);
            application.setWebApplicationType(WebApplicationType.NONE);
            application.run(args);
        } else {
            SpringApplication.run(PcBuilderApplication.class, args);
        }
    }
}
