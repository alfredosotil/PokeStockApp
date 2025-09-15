package com.poke.app.service.mapper;

import com.poke.app.domain.Card;
import com.poke.app.domain.InventoryItem;
import com.poke.app.domain.PokeUser;
import com.poke.app.service.dto.CardDTO;
import com.poke.app.service.dto.InventoryItemDTO;
import com.poke.app.service.dto.PokeUserDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link InventoryItem} and its DTO {@link InventoryItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface InventoryItemMapper extends EntityMapper<InventoryItemDTO, InventoryItem> {
    @Mapping(target = "card", source = "card", qualifiedByName = "cardName")
    @Mapping(target = "owner", source = "owner", qualifiedByName = "pokeUserDisplayName")
    InventoryItemDTO toDto(InventoryItem s);

    @Named("cardName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CardDTO toDtoCardName(Card card);

    @Named("pokeUserDisplayName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "displayName", source = "displayName")
    PokeUserDTO toDtoPokeUserDisplayName(PokeUser pokeUser);
}
