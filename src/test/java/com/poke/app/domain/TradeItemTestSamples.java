package com.poke.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TradeItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static TradeItem getTradeItemSample1() {
        return new TradeItem().id(1L).quantity(1).side("side1");
    }

    public static TradeItem getTradeItemSample2() {
        return new TradeItem().id(2L).quantity(2).side("side2");
    }

    public static TradeItem getTradeItemRandomSampleGenerator() {
        return new TradeItem().id(longCount.incrementAndGet()).quantity(intCount.incrementAndGet()).side(UUID.randomUUID().toString());
    }
}
