package com.poke.app.service.integration.pokemontcg.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PokemonTcgCardPriceDto {

    private PokemonTcgTcgPlayerPriceDto tcgplayer;
    private PokemonTcgCardMarketPriceDto cardmarket;
}
