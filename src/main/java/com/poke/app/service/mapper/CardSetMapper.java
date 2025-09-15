package com.poke.app.service.mapper;

import com.poke.app.domain.CardSet;
import com.poke.app.service.dto.CardSetDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link CardSet} and its DTO {@link CardSetDTO}.
 */
@Mapper(componentModel = "spring")
public interface CardSetMapper extends EntityMapper<CardSetDTO, CardSet> {}
