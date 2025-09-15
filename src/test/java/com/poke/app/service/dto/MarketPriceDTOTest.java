package com.poke.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.poke.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MarketPriceDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(MarketPriceDTO.class);
        MarketPriceDTO marketPriceDTO1 = new MarketPriceDTO();
        marketPriceDTO1.setId(1L);
        MarketPriceDTO marketPriceDTO2 = new MarketPriceDTO();
        assertThat(marketPriceDTO1).isNotEqualTo(marketPriceDTO2);
        marketPriceDTO2.setId(marketPriceDTO1.getId());
        assertThat(marketPriceDTO1).isEqualTo(marketPriceDTO2);
        marketPriceDTO2.setId(2L);
        assertThat(marketPriceDTO1).isNotEqualTo(marketPriceDTO2);
        marketPriceDTO1.setId(null);
        assertThat(marketPriceDTO1).isNotEqualTo(marketPriceDTO2);
    }
}
