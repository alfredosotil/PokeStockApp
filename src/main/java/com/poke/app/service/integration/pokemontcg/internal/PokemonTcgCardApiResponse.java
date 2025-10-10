package com.poke.app.service.integration.pokemontcg.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PokemonTcgCardApiResponse {

    private PokemonTcgCard data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PokemonTcgCard {

        private String id;
        private String name;
        private String supertype;
        private List<String> types;
        private String number;
        private String rarity;
        private Map<String, String> legalities;
        private PokemonTcgSet set;
        private PokemonTcgImages images;
        private PokemonTcgTcgPlayer tcgplayer;
        private PokemonTcgCardMarket cardmarket;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PokemonTcgSet {

        private String id;
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PokemonTcgImages {

        private String small;
        private String large;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PokemonTcgTcgPlayer {

        private Map<String, PokemonTcgPriceDetail> prices;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PokemonTcgPriceDetail {

        private BigDecimal low;
        private BigDecimal mid;
        private BigDecimal high;
        private BigDecimal market;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PokemonTcgCardMarket {

        private PokemonTcgCardMarketPrices prices;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PokemonTcgCardMarketPrices {

        private BigDecimal averageSellPrice;
        private BigDecimal lowPrice;
        private BigDecimal trendPrice;
    }
}
