package com.poke.app.service.dto;

import com.poke.app.domain.enumeration.MarketSource;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.poke.app.domain.MarketPrice} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MarketSource getSource() {
        return source;
    }

    public void setSource(MarketSource source) {
        this.source = source;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getPriceLow() {
        return priceLow;
    }

    public void setPriceLow(BigDecimal priceLow) {
        this.priceLow = priceLow;
    }

    public BigDecimal getPriceMid() {
        return priceMid;
    }

    public void setPriceMid(BigDecimal priceMid) {
        this.priceMid = priceMid;
    }

    public BigDecimal getPriceHigh() {
        return priceHigh;
    }

    public void setPriceHigh(BigDecimal priceHigh) {
        this.priceHigh = priceHigh;
    }

    public Instant getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public CardDTO getCard() {
        return card;
    }

    public void setCard(CardDTO card) {
        this.card = card;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MarketPriceDTO)) {
            return false;
        }

        MarketPriceDTO marketPriceDTO = (MarketPriceDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, marketPriceDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MarketPriceDTO{" +
            "id=" + getId() +
            ", source='" + getSource() + "'" +
            ", currency='" + getCurrency() + "'" +
            ", priceLow=" + getPriceLow() +
            ", priceMid=" + getPriceMid() +
            ", priceHigh=" + getPriceHigh() +
            ", lastUpdated='" + getLastUpdated() + "'" +
            ", card=" + getCard() +
            "}";
    }
}
