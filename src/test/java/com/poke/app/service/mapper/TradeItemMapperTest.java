package com.poke.app.service.mapper;

import static com.poke.app.domain.TradeItemAsserts.*;
import static com.poke.app.domain.TradeItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TradeItemMapperTest {

    private TradeItemMapper tradeItemMapper;

    @BeforeEach
    void setUp() {
        tradeItemMapper = new TradeItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTradeItemSample1();
        var actual = tradeItemMapper.toEntity(tradeItemMapper.toDto(expected));
        assertTradeItemAllPropertiesEquals(expected, actual);
    }
}
