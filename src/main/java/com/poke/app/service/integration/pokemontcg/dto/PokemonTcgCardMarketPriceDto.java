package com.poke.app.service.integration.pokemontcg.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PokemonTcgCardMarketPriceDto {

    private BigDecimal averageSellPrice;
    private BigDecimal lowPrice;
    private BigDecimal trendPrice;
}
