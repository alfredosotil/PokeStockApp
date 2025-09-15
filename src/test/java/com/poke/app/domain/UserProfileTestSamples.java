package com.poke.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UserProfileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UserProfile getUserProfileSample1() {
        return new UserProfile().id(1L).bio("bio1").location("location1").favoriteSet("favoriteSet1").playstyle("playstyle1");
    }

    public static UserProfile getUserProfileSample2() {
        return new UserProfile().id(2L).bio("bio2").location("location2").favoriteSet("favoriteSet2").playstyle("playstyle2");
    }

    public static UserProfile getUserProfileRandomSampleGenerator() {
        return new UserProfile()
            .id(longCount.incrementAndGet())
            .bio(UUID.randomUUID().toString())
            .location(UUID.randomUUID().toString())
            .favoriteSet(UUID.randomUUID().toString())
            .playstyle(UUID.randomUUID().toString());
    }
}
