package com.poke.app.domain;

import static com.poke.app.domain.CardTestSamples.*;
import static com.poke.app.domain.MarketPriceTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.poke.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MarketPriceTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MarketPrice.class);
        MarketPrice marketPrice1 = getMarketPriceSample1();
        MarketPrice marketPrice2 = new MarketPrice();
        assertThat(marketPrice1).isNotEqualTo(marketPrice2);

        marketPrice2.setId(marketPrice1.getId());
        assertThat(marketPrice1).isEqualTo(marketPrice2);

        marketPrice2 = getMarketPriceSample2();
        assertThat(marketPrice1).isNotEqualTo(marketPrice2);
    }

    @Test
    void cardTest() {
        MarketPrice marketPrice = getMarketPriceRandomSampleGenerator();
        Card cardBack = getCardRandomSampleGenerator();

        marketPrice.setCard(cardBack);
        assertThat(marketPrice.getCard()).isEqualTo(cardBack);

        marketPrice.card(null);
        assertThat(marketPrice.getCard()).isNull();
    }
}
