package com.poke.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.poke.app.domain.TradeItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TradeItemDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer quantity;

    @NotNull
    private String side;

    private TradeDTO trade;

    private CardDTO card;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSide() {
        return side;
    }

    public void setSide(String side) {
        this.side = side;
    }

    public TradeDTO getTrade() {
        return trade;
    }

    public void setTrade(TradeDTO trade) {
        this.trade = trade;
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
        if (!(o instanceof TradeItemDTO)) {
            return false;
        }

        TradeItemDTO tradeItemDTO = (TradeItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tradeItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TradeItemDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", side='" + getSide() + "'" +
            ", trade=" + getTrade() +
            ", card=" + getCard() +
            "}";
    }
}
