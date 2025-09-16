package com.poke.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.poke.app.domain.enumeration.MarketSource;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A MarketPrice.
 */
@Entity
@Table(name = "market_price")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MarketPrice implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private MarketSource source;

    @NotNull
    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "price_low", precision = 21, scale = 2)
    private BigDecimal priceLow;

    @Column(name = "price_mid", precision = 21, scale = 2)
    private BigDecimal priceMid;

    @Column(name = "price_high", precision = 21, scale = 2)
    private BigDecimal priceHigh;

    @NotNull
    @Column(name = "last_updated", nullable = false)
    private Instant lastUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "set" }, allowSetters = true)
    private Card card;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MarketPrice id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MarketSource getSource() {
        return this.source;
    }

    public MarketPrice source(MarketSource source) {
        this.setSource(source);
        return this;
    }

    public void setSource(MarketSource source) {
        this.source = source;
    }

    public String getCurrency() {
        return this.currency;
    }

    public MarketPrice currency(String currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getPriceLow() {
        return this.priceLow;
    }

    public MarketPrice priceLow(BigDecimal priceLow) {
        this.setPriceLow(priceLow);
        return this;
    }

    public void setPriceLow(BigDecimal priceLow) {
        this.priceLow = priceLow;
    }

    public BigDecimal getPriceMid() {
        return this.priceMid;
    }

    public MarketPrice priceMid(BigDecimal priceMid) {
        this.setPriceMid(priceMid);
        return this;
    }

    public void setPriceMid(BigDecimal priceMid) {
        this.priceMid = priceMid;
    }

    public BigDecimal getPriceHigh() {
        return this.priceHigh;
    }

    public MarketPrice priceHigh(BigDecimal priceHigh) {
        this.setPriceHigh(priceHigh);
        return this;
    }

    public void setPriceHigh(BigDecimal priceHigh) {
        this.priceHigh = priceHigh;
    }

    public Instant getLastUpdated() {
        return this.lastUpdated;
    }

    public MarketPrice lastUpdated(Instant lastUpdated) {
        this.setLastUpdated(lastUpdated);
        return this;
    }

    public void setLastUpdated(Instant lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public Card getCard() {
        return this.card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public MarketPrice card(Card card) {
        this.setCard(card);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MarketPrice)) {
            return false;
        }
        return getId() != null && getId().equals(((MarketPrice) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MarketPrice{" +
            "id=" + getId() +
            ", source='" + getSource() + "'" +
            ", currency='" + getCurrency() + "'" +
            ", priceLow=" + getPriceLow() +
            ", priceMid=" + getPriceMid() +
            ", priceHigh=" + getPriceHigh() +
            ", lastUpdated='" + getLastUpdated() + "'" +
            "}";
    }
}
