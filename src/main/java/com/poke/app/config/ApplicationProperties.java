package com.poke.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Poke Stock App.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@Getter
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();

    private final Market market = new Market();

    // jhipster-needle-application-properties-property
    // jhipster-needle-application-properties-property-getter

    @Getter
    @Setter
    public static class Liquibase {

        private Boolean asyncStart = true;
    }

    @Getter
    public static class Market {

        private final PokemonTcg pokemonTcg = new PokemonTcg();

        @Getter
        @Setter
        public static class PokemonTcg {

            private String baseUrl;

            private String apiKey;
        }
    }
    // jhipster-needle-application-properties-property-class
}
