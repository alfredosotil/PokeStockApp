package com.poke.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class CardTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Card getCardSample1() {
        return new Card()
            .id(1L)
            .tcgId("tcgId1")
            .setCode("setCode1")
            .number("number1")
            .name("name1")
            .rarity("rarity1")
            .superType("superType1")
            .types("types1")
            .imageUrl("imageUrl1")
            .legalities("legalities1");
    }

    public static Card getCardSample2() {
        return new Card()
            .id(2L)
            .tcgId("tcgId2")
            .setCode("setCode2")
            .number("number2")
            .name("name2")
            .rarity("rarity2")
            .superType("superType2")
            .types("types2")
            .imageUrl("imageUrl2")
            .legalities("legalities2");
    }

    public static Card getCardRandomSampleGenerator() {
        return new Card()
            .id(longCount.incrementAndGet())
            .tcgId(UUID.randomUUID().toString())
            .setCode(UUID.randomUUID().toString())
            .number(UUID.randomUUID().toString())
            .name(UUID.randomUUID().toString())
            .rarity(UUID.randomUUID().toString())
            .superType(UUID.randomUUID().toString())
            .types(UUID.randomUUID().toString())
            .imageUrl(UUID.randomUUID().toString())
            .legalities(UUID.randomUUID().toString());
    }
}
