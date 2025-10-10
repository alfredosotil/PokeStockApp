package com.poke.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PokemonTcgClientConfiguration {

    private static final String API_KEY_HEADER = "X-Api-Key";

    private final ApplicationProperties applicationProperties;

    public PokemonTcgClientConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean(name = "pokemonTcgWebClient")
    public WebClient pokemonTcgWebClient(WebClient.Builder webClientBuilder) {
        ApplicationProperties.PokemonTcg pokemonTcgProperties = applicationProperties.getPokemonTcg();
        WebClient.Builder builder = webClientBuilder
            .baseUrl(pokemonTcgProperties.getBaseUrl())
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        if (StringUtils.hasText(pokemonTcgProperties.getApiKey())) {
            builder.defaultHeader(API_KEY_HEADER, pokemonTcgProperties.getApiKey());
        }

        return builder.build();
    }
}
