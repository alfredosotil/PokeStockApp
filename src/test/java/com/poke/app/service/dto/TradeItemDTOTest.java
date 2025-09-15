package com.poke.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.poke.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TradeItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TradeItemDTO.class);
        TradeItemDTO tradeItemDTO1 = new TradeItemDTO();
        tradeItemDTO1.setId(1L);
        TradeItemDTO tradeItemDTO2 = new TradeItemDTO();
        assertThat(tradeItemDTO1).isNotEqualTo(tradeItemDTO2);
        tradeItemDTO2.setId(tradeItemDTO1.getId());
        assertThat(tradeItemDTO1).isEqualTo(tradeItemDTO2);
        tradeItemDTO2.setId(2L);
        assertThat(tradeItemDTO1).isNotEqualTo(tradeItemDTO2);
        tradeItemDTO1.setId(null);
        assertThat(tradeItemDTO1).isNotEqualTo(tradeItemDTO2);
    }
}
