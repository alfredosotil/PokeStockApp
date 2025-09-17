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
public class PokemonTcgCardData {

    @JsonProperty("tcgplayer")
    private PokemonTcgTcgplayer tcgplayer;

    public Optional<PokemonTcgPricesDTO> price() {
        return Optional.ofNullable(tcgplayer).flatMap(PokemonTcgTcgplayer::primaryPrice);
    }

    public Optional<Instant> updatedAt() {
        return Optional.ofNullable(tcgplayer).flatMap(PokemonTcgTcgplayer::updatedAtInstant);
    }
}
