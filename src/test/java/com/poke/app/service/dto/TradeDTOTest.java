package com.poke.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.poke.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TradeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TradeDTO.class);
        TradeDTO tradeDTO1 = new TradeDTO();
        tradeDTO1.setId(1L);
        TradeDTO tradeDTO2 = new TradeDTO();
        assertThat(tradeDTO1).isNotEqualTo(tradeDTO2);
        tradeDTO2.setId(tradeDTO1.getId());
        assertThat(tradeDTO1).isEqualTo(tradeDTO2);
        tradeDTO2.setId(2L);
        assertThat(tradeDTO1).isNotEqualTo(tradeDTO2);
        tradeDTO1.setId(null);
        assertThat(tradeDTO1).isNotEqualTo(tradeDTO2);
    }
}
