package com.poke.app.service.mapper;

import com.poke.app.domain.Card;
import com.poke.app.domain.MarketPrice;
import com.poke.app.service.dto.CardDTO;
import com.poke.app.service.dto.MarketPriceDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link MarketPrice} and its DTO {@link MarketPriceDTO}.
 */
@Mapper(componentModel = "spring")
public interface MarketPriceMapper extends EntityMapper<MarketPriceDTO, MarketPrice> {
    @Mapping(target = "card", source = "card", qualifiedByName = "cardName")
    MarketPriceDTO toDto(MarketPrice s);

    @Named("cardName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CardDTO toDtoCardName(Card card);
}
