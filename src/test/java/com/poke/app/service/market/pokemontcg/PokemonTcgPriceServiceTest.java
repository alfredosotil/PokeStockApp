package com.poke.app.service.market.pokemontcg;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.poke.app.config.ApplicationProperties;
import com.poke.app.domain.enumeration.MarketSource;
import com.poke.app.service.MarketPriceService;
import com.poke.app.service.dto.CardDTO;
import com.poke.app.service.dto.MarketPriceDTO;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

@ExtendWith(MockitoExtension.class)
class PokemonTcgPriceServiceTest {

    private static final DateTimeFormatter UPDATED_AT_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @Mock
    private MarketPriceService marketPriceService;

    private MockWebServer mockWebServer;

    private PokemonTcgPriceService pokemonTcgPriceService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        ApplicationProperties properties = new ApplicationProperties();
        properties.getMarket().getPokemonTcg().setBaseUrl(mockWebServer.url("/").toString());
        properties.getMarket().getPokemonTcg().setApiKey("test-api-key");

        PokemonTcgClient client = new PokemonTcgClient(properties, WebClient.builder());
        pokemonTcgPriceService = new PokemonTcgPriceService(client, marketPriceService);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void shouldFetchPriceAndPersist() throws Exception {
        mockWebServer.enqueue(
            new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "data": {
                        "tcgplayer": {
                          "updatedAt": "2024/02/29",
                          "prices": {
                            "holofoil": {
                              "low": 1.10,
                              "mid": 2.20
                            },
                            "normal": {
                              "low": 1.50,
                              "mid": 2.50,
                              "high": 3.50
                            }
                          }
                        }
                      }
                    }
                    """
                )
        );

        CardDTO card = createCard();

        when(marketPriceService.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<MarketPriceDTO> result = pokemonTcgPriceService.updateCardPrice(card);

        assertThat(result).isPresent();
        MarketPriceDTO saved = result.orElseThrow();
        assertThat(saved.getSource()).isEqualTo(MarketSource.POKEMON_TCG_API);
        assertThat(saved.getCurrency()).isEqualTo("USD");
        assertThat(saved.getPriceLow()).isEqualByComparingTo("1.50");
        assertThat(saved.getPriceMid()).isEqualByComparingTo("2.50");
        assertThat(saved.getPriceHigh()).isEqualByComparingTo("3.50");
        assertThat(saved.getCard()).isEqualTo(card);

        Instant expectedInstant = LocalDate.parse("2024/02/29", UPDATED_AT_FORMATTER).atStartOfDay(ZoneOffset.UTC).toInstant();
        assertThat(saved.getLastUpdated()).isEqualTo(expectedInstant);

        RecordedRequest recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        assertThat(recordedRequest).isNotNull();
        assertThat(recordedRequest.getMethod()).isEqualTo("GET");
        assertThat(recordedRequest.getPath()).isEqualTo("/v2/cards/xy7-54");
        assertThat(recordedRequest.getHeader(PokemonTcgClient.API_KEY_HEADER)).isEqualTo("test-api-key");

        verify(marketPriceService).save(any());
    }

    @Test
    void shouldNotPersistWhenNoPricesPresent() throws Exception {
        mockWebServer.enqueue(
            new MockResponse()
                .setHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "data": {
                        "tcgplayer": {
                          "updatedAt": "2024/02/29",
                          "prices": { }
                        }
                      }
                    }
                    """
                )
        );

        CardDTO card = createCard();

        Optional<MarketPriceDTO> result = pokemonTcgPriceService.updateCardPrice(card);

        assertThat(result).isEmpty();
        verify(marketPriceService, never()).save(any());

        RecordedRequest recordedRequest = mockWebServer.takeRequest(1, TimeUnit.SECONDS);
        assertThat(recordedRequest).isNotNull();
        assertThat(recordedRequest.getPath()).isEqualTo("/v2/cards/xy7-54");
    }

    private static CardDTO createCard() {
        CardDTO card = new CardDTO();
        card.setId(123L);
        card.setTcgId("xy7-54");
        card.setSetCode("xy7");
        card.setNumber("54");
        card.setName("Gardevoir");
        return card;
    }
}
