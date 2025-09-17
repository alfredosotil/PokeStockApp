package com.poke.app.service.market.pokemontcg;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PokemonTcgPricesDTO {

    public static final String DEFAULT_CURRENCY = "USD";

    @NonNull
    private final String currency;

    private final BigDecimal low;
    private final BigDecimal mid;
    private final BigDecimal high;
}
