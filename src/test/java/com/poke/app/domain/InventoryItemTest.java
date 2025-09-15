package com.poke.app.domain;

import static com.poke.app.domain.CardTestSamples.*;
import static com.poke.app.domain.InventoryItemTestSamples.*;
import static com.poke.app.domain.PokeUserTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.poke.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class InventoryItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(InventoryItem.class);
        InventoryItem inventoryItem1 = getInventoryItemSample1();
        InventoryItem inventoryItem2 = new InventoryItem();
        assertThat(inventoryItem1).isNotEqualTo(inventoryItem2);

        inventoryItem2.setId(inventoryItem1.getId());
        assertThat(inventoryItem1).isEqualTo(inventoryItem2);

        inventoryItem2 = getInventoryItemSample2();
        assertThat(inventoryItem1).isNotEqualTo(inventoryItem2);
    }

    @Test
    void cardTest() {
        InventoryItem inventoryItem = getInventoryItemRandomSampleGenerator();
        Card cardBack = getCardRandomSampleGenerator();

        inventoryItem.setCard(cardBack);
        assertThat(inventoryItem.getCard()).isEqualTo(cardBack);

        inventoryItem.card(null);
        assertThat(inventoryItem.getCard()).isNull();
    }

    @Test
    void ownerTest() {
        InventoryItem inventoryItem = getInventoryItemRandomSampleGenerator();
        PokeUser pokeUserBack = getPokeUserRandomSampleGenerator();

        inventoryItem.setOwner(pokeUserBack);
        assertThat(inventoryItem.getOwner()).isEqualTo(pokeUserBack);

        inventoryItem.owner(null);
        assertThat(inventoryItem.getOwner()).isNull();
    }
}
