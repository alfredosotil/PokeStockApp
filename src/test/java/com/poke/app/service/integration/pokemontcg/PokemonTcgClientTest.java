package com.poke.app.service.integration.pokemontcg;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.poke.app.service.integration.pokemontcg.dto.PokemonTcgCardDto;
import com.poke.app.service.integration.pokemontcg.dto.PokemonTcgCardPriceDto;
import com.poke.app.service.integration.pokemontcg.dto.PokemonTcgTcgPlayerPriceDto;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

class PokemonTcgClientTest {

    private static final String API_KEY = "test-api-key";

    private MockWebServer mockWebServer;

    private PokemonTcgClient pokemonTcgClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient webClient = WebClient.builder().baseUrl(mockWebServer.url("/").toString()).defaultHeader("X-Api-Key", API_KEY).build();
        pokemonTcgClient = new PokemonTcgClient(webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void getCardByIdShouldReturnCardInformationWithPrices() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(200).setBody(successfulResponse()));

        Optional<PokemonTcgCardDto> result = pokemonTcgClient.getCardById("swsh4-1");

        assertThat(result).isPresent();
        PokemonTcgCardDto card = result.orElseThrow();
        assertThat(card.getId()).isEqualTo("swsh4-1");
        assertThat(card.getName()).isEqualTo("Celebi");
        assertThat(card.getSuperType()).isEqualTo("Pokémon");
        assertThat(card.getSetId()).isEqualTo("swsh4");
        assertThat(card.getSetName()).isEqualTo("Vivid Voltage");
        assertThat(card.getImageUrl()).isEqualTo("https://images.pokemontcg.io/swsh4/1_hires.png");
        assertThat(card.getLegalities()).containsEntry("standard", "Legal");

        PokemonTcgCardPriceDto price = card.getPrice();
        assertThat(price).isNotNull();
        PokemonTcgTcgPlayerPriceDto tcgPlayerPrice = price.getTcgplayer();
        assertThat(tcgPlayerPrice).isNotNull();
        assertThat(tcgPlayerPrice.getMarket()).isEqualByComparingTo(BigDecimal.valueOf(2.0));
        assertThat(price.getCardmarket()).isNotNull();
        assertThat(price.getCardmarket().getTrendPrice()).isEqualByComparingTo(BigDecimal.valueOf(2.1));

        RecordedRequest request = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        assertThat(request).isNotNull();
        assertThat(request.getPath()).isEqualTo("/cards/swsh4-1");
        assertThat(request.getHeader("X-Api-Key")).isEqualTo(API_KEY);
    }

    @Test
    void getCardByIdShouldReturnEmptyWhenNotFound() throws Exception {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        Optional<PokemonTcgCardDto> result = pokemonTcgClient.getCardById("unknown");

        assertThat(result).isEmpty();

        RecordedRequest request = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        assertThat(request).isNotNull();
        assertThat(request.getPath()).isEqualTo("/cards/unknown");
    }

    @Test
    void getCardByIdShouldThrowExceptionForServerErrors() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        assertThatThrownBy(() -> pokemonTcgClient.getCardById("swsh4-1"))
            .isInstanceOf(PokemonTcgClientException.class)
            .hasMessageContaining("Failed to fetch card from Pokemon TCG API");
    }

    private String successfulResponse() {
        return """
            {
              \"data\": {
                \"id\": \"swsh4-1\",
                \"name\": \"Celebi\",
                \"supertype\": \"Pokémon\",
                \"number\": \"1\",
                \"rarity\": \"Rare Holo\",
                \"types\": [\"Grass\"],
                \"legalities\": {\"standard\": \"Legal\"},
                \"set\": {
                  \"id\": \"swsh4\",
                  \"name\": \"Vivid Voltage\"
                },
                \"images\": {
                  \"small\": \"https://images.pokemontcg.io/swsh4/1.png\",
                  \"large\": \"https://images.pokemontcg.io/swsh4/1_hires.png\"
                },
                \"tcgplayer\": {
                  \"prices\": {
                    \"holofoil\": {
                      \"low\": 1.2,
                      \"mid\": 1.8,
                      \"high\": 4.5,
                      \"market\": 2.0
                    }
                  }
                },
                \"cardmarket\": {
                  \"prices\": {
                    \"averageSellPrice\": 1.9,
                    \"lowPrice\": 1.1,
                    \"trendPrice\": 2.1
                  }
                }
              }
            }
        """;
    }
}
