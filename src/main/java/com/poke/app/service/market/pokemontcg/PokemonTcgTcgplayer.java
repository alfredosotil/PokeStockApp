package com.poke.app.service.market.pokemontcg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonTcgTcgplayer {

    private static final DateTimeFormatter UPDATED_AT_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    @JsonProperty("updatedAt")
    private String updatedAt;

    @JsonProperty("prices")
    private Map<String, PokemonTcgCardPrice> prices;

    public Optional<Instant> updatedAtInstant() {
        if (!StringUtils.hasText(updatedAt)) {
            return Optional.empty();
        }
        try {
            LocalDate date = LocalDate.parse(updatedAt, UPDATED_AT_FORMATTER);
            return Optional.of(date.atStartOfDay(ZoneOffset.UTC).toInstant());
        } catch (DateTimeParseException ex) {
            log.debug("Unable to parse updatedAt [{}] from Pokemon TCG API", updatedAt, ex);
            return Optional.empty();
        }
    }

    public Optional<PokemonTcgPricesDTO> primaryPrice() {
        if (prices == null || prices.isEmpty()) {
            return Optional.empty();
        }

        return prices
            .values()
            .stream()
            .filter(Objects::nonNull)
            .filter(PokemonTcgCardPrice::hasPrices)
            .max(Comparator.comparingInt(PokemonTcgCardPrice::definedValues))
            .map(price -> new PokemonTcgPricesDTO(PokemonTcgPricesDTO.DEFAULT_CURRENCY, price.getLow(), price.getMid(), price.getHigh()));
    }
}
