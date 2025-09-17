package com.poke.app.service.market.pokemontcg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonTcgCardPrice {

    @JsonProperty("low")
    private BigDecimal low;

    @JsonProperty("mid")
    private BigDecimal mid;

    @JsonProperty("high")
    private BigDecimal high;

    @JsonProperty("market")
    private BigDecimal market;

    @JsonProperty("directLow")
    private BigDecimal directLow;

    public boolean hasPrices() {
        return low != null || mid != null || high != null;
    }

    public int definedValues() {
        int count = 0;
        if (low != null) {
            count++;
        }
        if (mid != null) {
            count++;
        }
        if (high != null) {
            count++;
        }
        return count;
    }
}
