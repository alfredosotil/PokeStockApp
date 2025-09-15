package com.poke.app.domain;

import static com.poke.app.domain.PokeUserTestSamples.*;
import static com.poke.app.domain.TradeTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.poke.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TradeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Trade.class);
        Trade trade1 = getTradeSample1();
        Trade trade2 = new Trade();
        assertThat(trade1).isNotEqualTo(trade2);

        trade2.setId(trade1.getId());
        assertThat(trade1).isEqualTo(trade2);

        trade2 = getTradeSample2();
        assertThat(trade1).isNotEqualTo(trade2);
    }

    @Test
    void proposerTest() {
        Trade trade = getTradeRandomSampleGenerator();
        PokeUser pokeUserBack = getPokeUserRandomSampleGenerator();

        trade.setProposer(pokeUserBack);
        assertThat(trade.getProposer()).isEqualTo(pokeUserBack);

        trade.proposer(null);
        assertThat(trade.getProposer()).isNull();
    }
}
