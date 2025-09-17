package com.poke.app.service.market.pokemontcg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonTcgCardResponse {

    @JsonProperty("data")
    private PokemonTcgCardData data;

    public Optional<PokemonTcgPricesDTO> price() {
        return Optional.ofNullable(data).flatMap(PokemonTcgCardData::price);
    }

    public Optional<Instant> updatedAt() {
        return Optional.ofNullable(data).flatMap(PokemonTcgCardData::updatedAt);
    }
}
