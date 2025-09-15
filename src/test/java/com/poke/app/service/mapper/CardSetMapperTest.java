package com.poke.app.service.mapper;

import static com.poke.app.domain.CardSetAsserts.*;
import static com.poke.app.domain.CardSetTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CardSetMapperTest {

    private CardSetMapper cardSetMapper;

    @BeforeEach
    void setUp() {
        cardSetMapper = new CardSetMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getCardSetSample1();
        var actual = cardSetMapper.toEntity(cardSetMapper.toDto(expected));
        assertCardSetAllPropertiesEquals(expected, actual);
    }
}
