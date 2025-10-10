package com.poke.app.service.integration.pokemontcg;

import com.poke.app.service.integration.pokemontcg.dto.PokemonTcgCardDto;
import com.poke.app.service.integration.pokemontcg.dto.PokemonTcgCardMarketPriceDto;
import com.poke.app.service.integration.pokemontcg.dto.PokemonTcgCardPriceDto;
import com.poke.app.service.integration.pokemontcg.dto.PokemonTcgTcgPlayerPriceDto;
import com.poke.app.service.integration.pokemontcg.internal.PokemonTcgCardApiResponse;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class PokemonTcgClient {

    private static final Logger LOG = LoggerFactory.getLogger(PokemonTcgClient.class);

    private final WebClient webClient;

    public PokemonTcgClient(@Qualifier("pokemonTcgWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public Optional<PokemonTcgCardDto> getCardById(String cardId) {
        Assert.hasText(cardId, "cardId must not be blank");

        try {
            return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/cards/{id}").build(cardId))
                .exchangeToMono(clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.empty();
                    }

                    if (clientResponse.statusCode().is2xxSuccessful()) {
                        return clientResponse.bodyToMono(PokemonTcgCardApiResponse.class);
                    }

                    return clientResponse
                        .createException()
                        .flatMap(ex -> Mono.error(new PokemonTcgClientException("Failed to fetch card from Pokemon TCG API", ex)));
                })
                .map(PokemonTcgCardApiResponse::getData)
                .map(this::mapToCardDto)
                .blockOptional();
        } catch (PokemonTcgClientException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            LOG.error("Unexpected error retrieving card {} from Pokemon TCG API", cardId, exception);
            throw new PokemonTcgClientException("Failed to fetch card from Pokemon TCG API", exception);
        }
    }

    private PokemonTcgCardDto mapToCardDto(PokemonTcgCardApiResponse.PokemonTcgCard apiCard) {
        if (apiCard == null) {
            return null;
        }

        return PokemonTcgCardDto.builder()
            .id(apiCard.getId())
            .name(apiCard.getName())
            .superType(apiCard.getSupertype())
            .types(apiCard.getTypes())
            .number(apiCard.getNumber())
            .rarity(apiCard.getRarity())
            .setId(apiCard.getSet() != null ? apiCard.getSet().getId() : null)
            .setName(apiCard.getSet() != null ? apiCard.getSet().getName() : null)
            .imageUrl(extractImageUrl(apiCard.getImages()))
            .legalities(apiCard.getLegalities())
            .price(buildPrice(apiCard))
            .build();
    }

    private String extractImageUrl(PokemonTcgCardApiResponse.PokemonTcgImages images) {
        if (images == null) {
            return null;
        }

        if (StringUtils.hasText(images.getLarge())) {
            return images.getLarge();
        }

        return images.getSmall();
    }

    private PokemonTcgCardPriceDto buildPrice(PokemonTcgCardApiResponse.PokemonTcgCard apiCard) {
        PokemonTcgTcgPlayerPriceDto tcgplayerPrice = extractTcgPlayerPrice(apiCard.getTcgplayer());
        PokemonTcgCardMarketPriceDto cardMarketPrice = extractCardMarketPrice(apiCard.getCardmarket());

        if (tcgplayerPrice == null && cardMarketPrice == null) {
            return null;
        }

        return PokemonTcgCardPriceDto.builder().tcgplayer(tcgplayerPrice).cardmarket(cardMarketPrice).build();
    }

    private PokemonTcgTcgPlayerPriceDto extractTcgPlayerPrice(PokemonTcgCardApiResponse.PokemonTcgTcgPlayer tcgplayer) {
        if (tcgplayer == null || CollectionUtils.isEmpty(tcgplayer.getPrices())) {
            return null;
        }

        return tcgplayer
            .getPrices()
            .values()
            .stream()
            .filter(Objects::nonNull)
            .findFirst()
            .map(price ->
                PokemonTcgTcgPlayerPriceDto.builder()
                    .low(price.getLow())
                    .mid(price.getMid())
                    .high(price.getHigh())
                    .market(price.getMarket())
                    .build()
            )
            .orElse(null);
    }

    private PokemonTcgCardMarketPriceDto extractCardMarketPrice(PokemonTcgCardApiResponse.PokemonTcgCardMarket cardMarket) {
        if (cardMarket == null || cardMarket.getPrices() == null) {
            return null;
        }

        PokemonTcgCardApiResponse.PokemonTcgCardMarketPrices prices = cardMarket.getPrices();

        if (prices.getAverageSellPrice() == null && prices.getLowPrice() == null && prices.getTrendPrice() == null) {
            return null;
        }

        return PokemonTcgCardMarketPriceDto.builder()
            .averageSellPrice(prices.getAverageSellPrice())
            .lowPrice(prices.getLowPrice())
            .trendPrice(prices.getTrendPrice())
            .build();
    }
}
