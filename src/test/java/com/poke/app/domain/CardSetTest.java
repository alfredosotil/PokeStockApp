package com.poke.app.domain;

import static com.poke.app.domain.CardSetTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.poke.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CardSetTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CardSet.class);
        CardSet cardSet1 = getCardSetSample1();
        CardSet cardSet2 = new CardSet();
        assertThat(cardSet1).isNotEqualTo(cardSet2);

        cardSet2.setId(cardSet1.getId());
        assertThat(cardSet1).isEqualTo(cardSet2);

        cardSet2 = getCardSetSample2();
        assertThat(cardSet1).isNotEqualTo(cardSet2);
    }
}
