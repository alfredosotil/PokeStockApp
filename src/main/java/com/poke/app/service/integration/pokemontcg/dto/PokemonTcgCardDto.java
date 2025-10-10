package com.poke.app.service.integration.pokemontcg.dto;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PokemonTcgCardDto {

    private String id;
    private String name;
    private String superType;
    private List<String> types;
    private String number;
    private String rarity;
    private String setId;
    private String setName;
    private String imageUrl;
    private Map<String, String> legalities;
    private PokemonTcgCardPriceDto price;
}
