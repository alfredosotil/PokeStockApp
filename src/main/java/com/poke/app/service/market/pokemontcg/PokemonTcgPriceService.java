package com.poke.app.service.market.pokemontcg;

import com.poke.app.domain.enumeration.MarketSource;
import com.poke.app.service.MarketPriceService;
import com.poke.app.service.dto.CardDTO;
import com.poke.app.service.dto.MarketPriceDTO;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@Service
@RequiredArgsConstructor
public class PokemonTcgPriceService {

    private final PokemonTcgClient pokemonTcgClient;
    private final MarketPriceService marketPriceService;

    public Optional<MarketPriceDTO> updateCardPrice(CardDTO card) {
        Assert.notNull(card, "card must not be null");
        Assert.hasText(card.getTcgId(), "card must contain a tcgId");

        PokemonTcgCardResponse response = pokemonTcgClient.getCardById(card.getTcgId());
        Optional<PokemonTcgPricesDTO> optionalPrices = response.price();

        if (optionalPrices.isEmpty()) {
            log.debug("No Pokemon TCG pricing information available for card {}", card.getTcgId());
            return Optional.empty();
        }

        PokemonTcgPricesDTO prices = optionalPrices.orElseThrow();
        MarketPriceDTO marketPrice = new MarketPriceDTO();
        marketPrice.setSource(MarketSource.POKEMON_TCG_API);
        marketPrice.setCurrency(prices.getCurrency());
        marketPrice.setPriceLow(prices.getLow());
        marketPrice.setPriceMid(prices.getMid());
        marketPrice.setPriceHigh(prices.getHigh());
        marketPrice.setLastUpdated(response.updatedAt().orElseGet(Instant::now));
        marketPrice.setCard(card);

        MarketPriceDTO saved = marketPriceService.save(marketPrice);
        log.debug("Saved Pokemon TCG pricing for card {}", card.getTcgId());
        return Optional.ofNullable(saved);
    }
}
