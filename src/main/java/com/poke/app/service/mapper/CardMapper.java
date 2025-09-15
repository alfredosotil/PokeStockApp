package com.poke.app.service.mapper;

import com.poke.app.domain.Card;
import com.poke.app.domain.CardSet;
import com.poke.app.service.dto.CardDTO;
import com.poke.app.service.dto.CardSetDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Card} and its DTO {@link CardDTO}.
 */
@Mapper(componentModel = "spring")
public interface CardMapper extends EntityMapper<CardDTO, Card> {
    @Mapping(target = "set", source = "set", qualifiedByName = "cardSetCode")
    CardDTO toDto(Card s);

    @Named("cardSetCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    CardSetDTO toDtoCardSetCode(CardSet cardSet);
}
