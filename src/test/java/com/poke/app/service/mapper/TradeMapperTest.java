package com.poke.app.service.mapper;

import static com.poke.app.domain.TradeAsserts.*;
import static com.poke.app.domain.TradeTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TradeMapperTest {

    private TradeMapper tradeMapper;

    @BeforeEach
    void setUp() {
        tradeMapper = new TradeMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getTradeSample1();
        var actual = tradeMapper.toEntity(tradeMapper.toDto(expected));
        assertTradeAllPropertiesEquals(expected, actual);
    }
}
