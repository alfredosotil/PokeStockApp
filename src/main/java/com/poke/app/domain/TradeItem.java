package com.poke.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A TradeItem.
 */
@Entity
@Table(name = "trade_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TradeItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Min(value = 1)
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @NotNull
    @Column(name = "side", nullable = false)
    private String side;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "proposer" }, allowSetters = true)
    private Trade trade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "set" }, allowSetters = true)
    private Card card;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public TradeItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public TradeItem quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSide() {
        return this.side;
    }

    public TradeItem side(String side) {
        this.setSide(side);
        return this;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public Trade getTrade() {
        return this.trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    public TradeItem trade(Trade trade) {
        this.setTrade(trade);
        return this;
    }

    public Card getCard() {
        return this.card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public TradeItem card(Card card) {
        this.setCard(card);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TradeItem)) {
            return false;
        }
        return getId() != null && getId().equals(((TradeItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TradeItem{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", side='" + getSide() + "'" +
            "}";
    }
}
