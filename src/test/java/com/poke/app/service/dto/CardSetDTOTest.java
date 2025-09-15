package com.poke.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.poke.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CardSetDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CardSetDTO.class);
        CardSetDTO cardSetDTO1 = new CardSetDTO();
        cardSetDTO1.setId(1L);
        CardSetDTO cardSetDTO2 = new CardSetDTO();
        assertThat(cardSetDTO1).isNotEqualTo(cardSetDTO2);
        cardSetDTO2.setId(cardSetDTO1.getId());
        assertThat(cardSetDTO1).isEqualTo(cardSetDTO2);
        cardSetDTO2.setId(2L);
        assertThat(cardSetDTO1).isNotEqualTo(cardSetDTO2);
        cardSetDTO1.setId(null);
        assertThat(cardSetDTO1).isNotEqualTo(cardSetDTO2);
    }
}
