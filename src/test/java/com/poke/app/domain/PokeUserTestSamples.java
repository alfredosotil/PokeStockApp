package com.poke.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PokeUserTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static PokeUser getPokeUserSample1() {
        return new PokeUser().id(1L).displayName("displayName1").country("country1");
    }

    public static PokeUser getPokeUserSample2() {
        return new PokeUser().id(2L).displayName("displayName2").country("country2");
    }

    public static PokeUser getPokeUserRandomSampleGenerator() {
        return new PokeUser()
            .id(longCount.incrementAndGet())
            .displayName(UUID.randomUUID().toString())
            .country(UUID.randomUUID().toString());
    }
}
