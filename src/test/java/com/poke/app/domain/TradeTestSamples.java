package com.poke.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TradeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Trade getTradeSample1() {
        return new Trade().id(1L).message("message1");
    }

    public static Trade getTradeSample2() {
        return new Trade().id(2L).message("message2");
    }

    public static Trade getTradeRandomSampleGenerator() {
        return new Trade().id(longCount.incrementAndGet()).message(UUID.randomUUID().toString());
    }
}
