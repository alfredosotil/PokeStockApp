package com.poke.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class InventoryItemTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static InventoryItem getInventoryItemSample1() {
        return new InventoryItem().id(1L).quantity(1).grade("grade1").notes("notes1");
    }

    public static InventoryItem getInventoryItemSample2() {
        return new InventoryItem().id(2L).quantity(2).grade("grade2").notes("notes2");
    }

    public static InventoryItem getInventoryItemRandomSampleGenerator() {
        return new InventoryItem()
            .id(longCount.incrementAndGet())
            .quantity(intCount.incrementAndGet())
            .grade(UUID.randomUUID().toString())
            .notes(UUID.randomUUID().toString());
    }
}
