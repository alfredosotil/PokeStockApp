package com.poke.app.service.dto;

import com.poke.app.domain.enumeration.MarketSource;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A DTO for the {@link com.poke.app.domain.MarketPrice} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MarketPriceDTO implements Serializable {

    private Long id;

    @NotNull
    private MarketSource source;

    @NotNull
    private String currency;

    private BigDecimal priceLow;

    private BigDecimal priceMid;

    private BigDecimal priceHigh;

    @NotNull
    private Instant lastUpdated;

    private CardDTO card;

    @EqualsAndHashCode.Include
    private Object equalityIdentifier() {
        return id != null ? id : System.identityHashCode(this);
    }
}
