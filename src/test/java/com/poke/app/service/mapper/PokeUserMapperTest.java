package com.poke.app.service.mapper;

import static com.poke.app.domain.PokeUserAsserts.*;
import static com.poke.app.domain.PokeUserTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PokeUserMapperTest {

    private PokeUserMapper pokeUserMapper;

    @BeforeEach
    void setUp() {
        pokeUserMapper = new PokeUserMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getPokeUserSample1();
        var actual = pokeUserMapper.toEntity(pokeUserMapper.toDto(expected));
        assertPokeUserAllPropertiesEquals(expected, actual);
    }
}
