package com.poke.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class MarketPriceTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static MarketPrice getMarketPriceSample1() {
        return new MarketPrice().id(1L).currency("currency1");
    }

    public static MarketPrice getMarketPriceSample2() {
        return new MarketPrice().id(2L).currency("currency2");
    }

    public static MarketPrice getMarketPriceRandomSampleGenerator() {
        return new MarketPrice().id(longCount.incrementAndGet()).currency(UUID.randomUUID().toString());
    }
}
