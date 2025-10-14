package com.poke.app.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.poke.app.domain.Card;
import com.poke.app.domain.MarketPrice;
import com.poke.app.domain.enumeration.MarketSource;
import com.poke.app.repository.CardRepository;
import com.poke.app.repository.MarketPriceRepository;
import com.poke.app.service.integration.pokemontcg.PokemonTcgClient;
import com.poke.app.service.integration.pokemontcg.dto.PokemonTcgCardDto;
import com.poke.app.service.integration.pokemontcg.dto.PokemonTcgCardMarketPriceDto;
import com.poke.app.service.integration.pokemontcg.dto.PokemonTcgCardPriceDto;
import com.poke.app.service.integration.pokemontcg.dto.PokemonTcgTcgPlayerPriceDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MarketPriceRefreshServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private MarketPriceRepository marketPriceRepository;

    @Mock
    private PokemonTcgClient pokemonTcgClient;

    @InjectMocks
    private MarketPriceRefreshService marketPriceRefreshService;

    @Captor
    private ArgumentCaptor<MarketPrice> marketPriceCaptor;

    private Card buildCard() {
        return new Card().id(1L).tcgId("xy1-1").setCode("XY").number("1").name("Pikachu");
    }

    @BeforeEach
    void setUp() {
        // marketPriceRefreshService is injected via @InjectMocks, but we ensure it's created before each test.
    }

    @Test
    void refreshAllCardPricesShouldPersistLatestValuesFromPokemonTcgApi() {
        Card card = buildCard();

        PokemonTcgTcgPlayerPriceDto tcgPlayerPrice = PokemonTcgTcgPlayerPriceDto.builder()
            .low(new BigDecimal("1.11"))
            .mid(null)
            .high(new BigDecimal("3.33"))
            .market(new BigDecimal("2.22"))
            .build();

        PokemonTcgCardMarketPriceDto cardMarketPrice = PokemonTcgCardMarketPriceDto.builder()
            .lowPrice(new BigDecimal("4.44"))
            .trendPrice(new BigDecimal("5.55"))
            .averageSellPrice(new BigDecimal("6.66"))
            .build();

        PokemonTcgCardDto cardDto = PokemonTcgCardDto.builder()
            .id(card.getTcgId())
            .price(PokemonTcgCardPriceDto.builder().tcgplayer(tcgPlayerPrice).cardmarket(cardMarketPrice).build())
            .build();

        when(cardRepository.findAll()).thenReturn(List.of(card));
        when(pokemonTcgClient.getCardById(card.getTcgId())).thenReturn(Optional.of(cardDto));
        when(marketPriceRepository.findByCard_IdAndSource(card.getId(), MarketSource.TCGPLAYER)).thenReturn(Optional.empty());
        when(marketPriceRepository.findByCard_IdAndSource(card.getId(), MarketSource.POKEMON_TCG_API)).thenReturn(Optional.empty());

        marketPriceRefreshService.refreshAllCardPrices();

        verify(cardRepository).findAll();
        verify(pokemonTcgClient).getCardById(card.getTcgId());
        verify(marketPriceRepository, times(2)).save(marketPriceCaptor.capture());

        List<MarketPrice> savedPrices = marketPriceCaptor.getAllValues();
        assertThat(savedPrices).hasSize(2);

        MarketPrice tcgPlayerSaved = savedPrices
            .stream()
            .filter(price -> price.getSource() == MarketSource.TCGPLAYER)
            .findFirst()
            .orElseThrow();
        MarketPrice cardMarketSaved = savedPrices
            .stream()
            .filter(price -> price.getSource() == MarketSource.POKEMON_TCG_API)
            .findFirst()
            .orElseThrow();

        assertThat(tcgPlayerSaved.getCard()).isEqualTo(card);
        assertThat(tcgPlayerSaved.getCurrency()).isEqualTo("USD");
        assertThat(tcgPlayerSaved.getPriceLow()).isEqualByComparingTo("1.11");
        assertThat(tcgPlayerSaved.getPriceMid()).isEqualByComparingTo("2.22");
        assertThat(tcgPlayerSaved.getPriceHigh()).isEqualByComparingTo("3.33");
        assertThat(tcgPlayerSaved.getLastUpdated()).isNotNull();

        assertThat(cardMarketSaved.getCard()).isEqualTo(card);
        assertThat(cardMarketSaved.getCurrency()).isEqualTo("EUR");
        assertThat(cardMarketSaved.getPriceLow()).isEqualByComparingTo("4.44");
        assertThat(cardMarketSaved.getPriceMid()).isEqualByComparingTo("5.55");
        assertThat(cardMarketSaved.getPriceHigh()).isEqualByComparingTo("6.66");
        assertThat(cardMarketSaved.getLastUpdated()).isNotNull();
    }

    @Test
    void refreshAllCardPricesShouldSkipCardsWithoutTcgId() {
        Card cardWithoutTcgId = new Card().id(2L);

        when(cardRepository.findAll()).thenReturn(List.of(cardWithoutTcgId));

        marketPriceRefreshService.refreshAllCardPrices();

        verify(cardRepository).findAll();
        verifyNoInteractions(pokemonTcgClient);
        verify(marketPriceRepository, never()).save(any());
    }
}
