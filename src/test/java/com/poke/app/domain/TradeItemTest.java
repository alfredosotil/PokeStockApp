package com.poke.app.domain;

import static com.poke.app.domain.CardTestSamples.*;
import static com.poke.app.domain.TradeItemTestSamples.*;
import static com.poke.app.domain.TradeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.poke.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TradeItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TradeItem.class);
        TradeItem tradeItem1 = getTradeItemSample1();
        TradeItem tradeItem2 = new TradeItem();
        assertThat(tradeItem1).isNotEqualTo(tradeItem2);

        tradeItem2.setId(tradeItem1.getId());
        assertThat(tradeItem1).isEqualTo(tradeItem2);

        tradeItem2 = getTradeItemSample2();
        assertThat(tradeItem1).isNotEqualTo(tradeItem2);
    }

    @Test
    void tradeTest() {
        TradeItem tradeItem = getTradeItemRandomSampleGenerator();
        Trade tradeBack = getTradeRandomSampleGenerator();

        tradeItem.setTrade(tradeBack);
        assertThat(tradeItem.getTrade()).isEqualTo(tradeBack);

        tradeItem.trade(null);
        assertThat(tradeItem.getTrade()).isNull();
    }

    @Test
    void cardTest() {
        TradeItem tradeItem = getTradeItemRandomSampleGenerator();
        Card cardBack = getCardRandomSampleGenerator();

        tradeItem.setCard(cardBack);
        assertThat(tradeItem.getCard()).isEqualTo(cardBack);

        tradeItem.card(null);
        assertThat(tradeItem.getCard()).isNull();
    }
}
