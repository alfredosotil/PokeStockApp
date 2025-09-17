package com.poke.app.service.market.pokemontcg;

import com.poke.app.config.ApplicationProperties;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class PokemonTcgClient {

    static final String API_KEY_HEADER = "X-Api-Key";

    private final WebClient webClient;

    public PokemonTcgClient(ApplicationProperties applicationProperties, WebClient.Builder webClientBuilder) {
        Assert.notNull(applicationProperties, "applicationProperties must not be null");
        Assert.notNull(webClientBuilder, "webClientBuilder must not be null");

        var configuration = applicationProperties.getMarket().getPokemonTcg();
        Assert.notNull(configuration, "Pokemon TCG configuration must not be null");
        Assert.hasText(configuration.getBaseUrl(), "Pokemon TCG API base url must not be empty");

        String apiKey = Objects.toString(configuration.getApiKey(), "");
        this.webClient = webClientBuilder.clone().baseUrl(configuration.getBaseUrl()).defaultHeader(API_KEY_HEADER, apiKey).build();
    }

    public PokemonTcgCardResponse getCardById(String cardId) {
        Assert.hasText(cardId, "cardId must not be blank");
        log.debug("Requesting card {} from Pokemon TCG API", cardId);
        PokemonTcgCardResponse response = webClient
            .get()
            .uri(uriBuilder -> uriBuilder.path("/v2/cards/{id}").build(cardId))
            .retrieve()
            .bodyToMono(PokemonTcgCardResponse.class)
            .block();
        return Objects.requireNonNull(response, "Pokemon TCG API returned an empty body");
    }

    public Optional<PokemonTcgPricesDTO> getCardPrices(String cardId) {
        return getCardById(cardId).price();
    }
}
