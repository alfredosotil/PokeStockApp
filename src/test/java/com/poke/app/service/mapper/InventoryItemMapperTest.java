package com.poke.app.service.mapper;

import static com.poke.app.domain.InventoryItemAsserts.*;
import static com.poke.app.domain.InventoryItemTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class InventoryItemMapperTest {

    private InventoryItemMapper inventoryItemMapper;

    @BeforeEach
    void setUp() {
        inventoryItemMapper = new InventoryItemMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getInventoryItemSample1();
        var actual = inventoryItemMapper.toEntity(inventoryItemMapper.toDto(expected));
        assertInventoryItemAllPropertiesEquals(expected, actual);
    }
}
