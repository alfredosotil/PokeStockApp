package com.poke.app.service.dto;

import com.poke.app.domain.enumeration.CardCondition;
import com.poke.app.domain.enumeration.CardLanguage;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * A DTO for the {@link com.poke.app.domain.InventoryItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InventoryItemDTO implements Serializable {

    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer quantity;

    @NotNull
    private CardCondition condition;

    @NotNull
    private CardLanguage language;

    @NotNull
    private Boolean graded;

    private String grade;

    private BigDecimal purchasePrice;

    private String notes;

    private CardDTO card;

    private PokeUserDTO owner;

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

    public CardCondition getCondition() {
        return condition;
    }

    public void setCondition(CardCondition condition) {
        this.condition = condition;
    }

    public CardLanguage getLanguage() {
        return language;
    }

    public void setLanguage(CardLanguage language) {
        this.language = language;
    }

    public Boolean getGraded() {
        return graded;
    }

    public void setGraded(Boolean graded) {
        this.graded = graded;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public CardDTO getCard() {
        return card;
    }

    public void setCard(CardDTO card) {
        this.card = card;
    }

    public PokeUserDTO getOwner() {
        return owner;
    }

    public void setOwner(PokeUserDTO owner) {
        this.owner = owner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InventoryItemDTO)) {
            return false;
        }

        InventoryItemDTO inventoryItemDTO = (InventoryItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, inventoryItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InventoryItemDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", condition='" + getCondition() + "'" +
            ", language='" + getLanguage() + "'" +
            ", graded='" + getGraded() + "'" +
            ", grade='" + getGrade() + "'" +
            ", purchasePrice=" + getPurchasePrice() +
            ", notes='" + getNotes() + "'" +
            ", card=" + getCard() +
            ", owner=" + getOwner() +
            "}";
    }
}
