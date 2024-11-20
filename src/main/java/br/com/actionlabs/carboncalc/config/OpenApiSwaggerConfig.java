package br.com.actionlabs.carboncalc.config;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Paths;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Comparator;
import java.util.TreeMap;


@Configuration

@OpenAPIDefinition(
        info = @Info(
                title = "Carbon Calculator API",
                version = "v1",
                description = "The Carbon Calculator API allows users to calculate their carbon footprint by providing energy consumption, transportation details, and solid waste production. Built with Java 17, Spring Boot, and MongoDB, this application ensures data persistence and scalability.",
                contact = @Contact(name = "Arthur Felix", email = "dev.felixarthur@gmail.com")
        ),
        externalDocs = @ExternalDocumentation(description = "Complete documentation about the API functionalities and architecture")
)
public class OpenApiSwaggerConfig {
  @Value("${server.version}")
  private String version;
  @Autowired
  private Environment env;

  @Bean
  public GroupedOpenApi publicApi() {
    return GroupedOpenApi.builder()
        .group("spring")
        .packagesToScan("br.com.actionlabs.carboncalc.controller")
        .addOpenApiCustomizer(sortPathsAlphabetically())
        .build();
  }

  private OpenApiCustomizer sortPathsAlphabetically() {
    return openApi -> {
      TreeMap<String, io.swagger.v3.oas.models.PathItem> sortedPaths = new TreeMap<>(Comparator.naturalOrder());
      sortedPaths.putAll(openApi.getPaths());

      Paths paths = new Paths();
      paths.putAll(sortedPaths);

      openApi.setPaths(paths);
    };
  }

}