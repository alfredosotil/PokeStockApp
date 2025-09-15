package com.poke.app.domain;

import static com.poke.app.domain.PokeUserTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.poke.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PokeUserTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PokeUser.class);
        PokeUser pokeUser1 = getPokeUserSample1();
        PokeUser pokeUser2 = new PokeUser();
        assertThat(pokeUser1).isNotEqualTo(pokeUser2);

        pokeUser2.setId(pokeUser1.getId());
        assertThat(pokeUser1).isEqualTo(pokeUser2);

        pokeUser2 = getPokeUserSample2();
        assertThat(pokeUser1).isNotEqualTo(pokeUser2);
    }
}
