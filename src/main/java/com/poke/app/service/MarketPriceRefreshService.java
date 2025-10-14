package com.poke.app.service;

import com.poke.app.domain.Card;
import com.poke.app.domain.MarketPrice;
import com.poke.app.domain.enumeration.MarketSource;
import com.poke.app.repository.CardRepository;
import com.poke.app.repository.MarketPriceRepository;
import com.poke.app.service.integration.pokemontcg.PokemonTcgClient;
import com.poke.app.service.integration.pokemontcg.PokemonTcgClientException;
import com.poke.app.service.integration.pokemontcg.dto.PokemonTcgCardDto;
import com.poke.app.service.integration.pokemontcg.dto.PokemonTcgCardMarketPriceDto;
import com.poke.app.service.integration.pokemontcg.dto.PokemonTcgCardPriceDto;
import com.poke.app.service.integration.pokemontcg.dto.PokemonTcgTcgPlayerPriceDto;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional
public class MarketPriceRefreshService {

    private static final Logger LOG = LoggerFactory.getLogger(MarketPriceRefreshService.class);

    private static final String CARDMARKET_CURRENCY = "EUR";
    private static final String TCGPLAYER_CURRENCY = "USD";

    private final CardRepository cardRepository;
    private final MarketPriceRepository marketPriceRepository;
    private final PokemonTcgClient pokemonTcgClient;

    public MarketPriceRefreshService(
        CardRepository cardRepository,
        MarketPriceRepository marketPriceRepository,
        PokemonTcgClient pokemonTcgClient
    ) {
        this.cardRepository = cardRepository;
        this.marketPriceRepository = marketPriceRepository;
        this.pokemonTcgClient = pokemonTcgClient;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void refreshAllCardPrices() {
        LOG.info("Starting scheduled refresh of card market prices");

        List<Card> cards = cardRepository.findAll();

        if (cards.isEmpty()) {
            LOG.info("No cards found to refresh market prices");
            return;
        }

        cards.forEach(this::refreshCardPrices);
    }

    private void refreshCardPrices(Card card) {
        if (card == null || !StringUtils.hasText(card.getTcgId())) {
            LOG.debug("Skipping card {} because it lacks a tcgId", card != null ? card.getId() : null);
            return;
        }

        try {
            Optional<PokemonTcgCardDto> maybeCard = pokemonTcgClient.getCardById(card.getTcgId());

            PokemonTcgCardDto cardDto = maybeCard.orElse(null);

            if (cardDto == null) {
                LOG.debug("Card {} not found in Pokemon TCG API", card.getTcgId());
                return;
            }
            PokemonTcgCardPriceDto priceDto = cardDto.getPrice();

            if (priceDto == null) {
                LOG.debug("Card {} returned without price information", card.getTcgId());
                return;
            }

            Instant now = Instant.now();
            updateTcgPlayerPrice(card, priceDto.getTcgplayer(), now);
            updateCardMarketPrice(card, priceDto.getCardmarket(), now);
        } catch (PokemonTcgClientException exception) {
            LOG.warn("Failed to refresh price information for card {}: {}", card.getTcgId(), exception.getMessage());
        } catch (RuntimeException exception) {
            LOG.error("Unexpected error refreshing price information for card {}", card.getTcgId(), exception);
        }
    }

    private void updateTcgPlayerPrice(Card card, PokemonTcgTcgPlayerPriceDto tcgPlayerPrice, Instant updatedAt) {
        if (tcgPlayerPrice == null) {
            return;
        }

        MarketPrice marketPrice = marketPriceRepository
            .findByCard_IdAndSource(card.getId(), MarketSource.TCGPLAYER)
            .orElseGet(() -> new MarketPrice().card(card).source(MarketSource.TCGPLAYER));

        marketPrice.setCurrency(TCGPLAYER_CURRENCY);
        marketPrice.setPriceLow(tcgPlayerPrice.getLow());
        marketPrice.setPriceMid(firstNonNull(tcgPlayerPrice.getMid(), tcgPlayerPrice.getMarket()));
        marketPrice.setPriceHigh(tcgPlayerPrice.getHigh());
        marketPrice.setLastUpdated(updatedAt);

        marketPriceRepository.save(marketPrice);
    }

    private void updateCardMarketPrice(Card card, PokemonTcgCardMarketPriceDto cardMarketPrice, Instant updatedAt) {
        if (cardMarketPrice == null) {
            return;
        }

        MarketPrice marketPrice = marketPriceRepository
            .findByCard_IdAndSource(card.getId(), MarketSource.POKEMON_TCG_API)
            .orElseGet(() -> new MarketPrice().card(card).source(MarketSource.POKEMON_TCG_API));

        marketPrice.setCurrency(CARDMARKET_CURRENCY);
        marketPrice.setPriceLow(cardMarketPrice.getLowPrice());
        marketPrice.setPriceMid(cardMarketPrice.getTrendPrice());
        marketPrice.setPriceHigh(cardMarketPrice.getAverageSellPrice());
        marketPrice.setLastUpdated(updatedAt);

        marketPriceRepository.save(marketPrice);
    }

    private BigDecimal firstNonNull(BigDecimal... values) {
        if (values == null) {
            return null;
        }

        for (BigDecimal value : values) {
            if (value != null) {
                return value;
            }
        }

        return null;
    }
}
