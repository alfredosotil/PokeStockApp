package com.poke.app.service.mapper;

import static com.poke.app.domain.MarketPriceAsserts.*;
import static com.poke.app.domain.MarketPriceTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MarketPriceMapperTest {

    private MarketPriceMapper marketPriceMapper;

    @BeforeEach
    void setUp() {
        marketPriceMapper = new MarketPriceMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getMarketPriceSample1();
        var actual = marketPriceMapper.toEntity(marketPriceMapper.toDto(expected));
        assertMarketPriceAllPropertiesEquals(expected, actual);
    }
}
