package com.poke.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CardSetTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static CardSet getCardSetSample1() {
        return new CardSet().id(1L).code("code1").name("name1");
    }

    public static CardSet getCardSetSample2() {
        return new CardSet().id(2L).code("code2").name("name2");
    }

    public static CardSet getCardSetRandomSampleGenerator() {
        return new CardSet().id(longCount.incrementAndGet()).code(UUID.randomUUID().toString()).name(UUID.randomUUID().toString());
    }
}
