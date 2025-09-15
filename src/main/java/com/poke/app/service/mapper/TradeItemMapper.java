package com.poke.app.service.mapper;

import com.poke.app.domain.Card;
import com.poke.app.domain.Trade;
import com.poke.app.domain.TradeItem;
import com.poke.app.service.dto.CardDTO;
import com.poke.app.service.dto.TradeDTO;
import com.poke.app.service.dto.TradeItemDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link TradeItem} and its DTO {@link TradeItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface TradeItemMapper extends EntityMapper<TradeItemDTO, TradeItem> {
    @Mapping(target = "trade", source = "trade", qualifiedByName = "tradeId")
    @Mapping(target = "card", source = "card", qualifiedByName = "cardName")
    TradeItemDTO toDto(TradeItem s);

    @Named("tradeId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TradeDTO toDtoTradeId(Trade trade);

    @Named("cardName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CardDTO toDtoCardName(Card card);
}
