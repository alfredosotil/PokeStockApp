package com.poke.app.service.mapper;

import com.poke.app.domain.PokeUser;
import com.poke.app.domain.Trade;
import com.poke.app.service.dto.PokeUserDTO;
import com.poke.app.service.dto.TradeDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Trade} and its DTO {@link TradeDTO}.
 */
@Mapper(componentModel = "spring")
public interface TradeMapper extends EntityMapper<TradeDTO, Trade> {
    @Mapping(target = "proposer", source = "proposer", qualifiedByName = "pokeUserDisplayName")
    TradeDTO toDto(Trade s);

    @Named("pokeUserDisplayName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "displayName", source = "displayName")
    PokeUserDTO toDtoPokeUserDisplayName(PokeUser pokeUser);
}
