package com.poke.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.poke.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PokeUserDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PokeUserDTO.class);
        PokeUserDTO pokeUserDTO1 = new PokeUserDTO();
        pokeUserDTO1.setId(1L);
        PokeUserDTO pokeUserDTO2 = new PokeUserDTO();
        assertThat(pokeUserDTO1).isNotEqualTo(pokeUserDTO2);
        pokeUserDTO2.setId(pokeUserDTO1.getId());
        assertThat(pokeUserDTO1).isEqualTo(pokeUserDTO2);
        pokeUserDTO2.setId(2L);
        assertThat(pokeUserDTO1).isNotEqualTo(pokeUserDTO2);
        pokeUserDTO1.setId(null);
        assertThat(pokeUserDTO1).isNotEqualTo(pokeUserDTO2);
    }
}
