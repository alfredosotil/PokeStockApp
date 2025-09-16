package com.poke.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.poke.app.domain.enumeration.CardCondition;
import com.poke.app.domain.enumeration.CardLanguage;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A InventoryItem.
 */
@Entity
@Table(name = "inventory_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class InventoryItem implements Serializable {

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
    @Enumerated(EnumType.STRING)
    @Column(name = "condition", nullable = false)
    private CardCondition condition;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false)
    private CardLanguage language;

    @NotNull
    @Column(name = "graded", nullable = false)
    private Boolean graded;

    @Column(name = "grade")
    private String grade;

    @Column(name = "purchase_price", precision = 21, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "notes")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "set" }, allowSetters = true)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "user" }, allowSetters = true)
    private PokeUser owner;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public InventoryItem id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return this.quantity;
    }

    public InventoryItem quantity(Integer quantity) {
        this.setQuantity(quantity);
        return this;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public CardCondition getCondition() {
        return this.condition;
    }

    public InventoryItem condition(CardCondition condition) {
        this.setCondition(condition);
        return this;
    }

    public void setCondition(CardCondition condition) {
        this.condition = condition;
    }

    public CardLanguage getLanguage() {
        return this.language;
    }

    public InventoryItem language(CardLanguage language) {
        this.setLanguage(language);
        return this;
    }

    public void setLanguage(CardLanguage language) {
        this.language = language;
    }

    public Boolean getGraded() {
        return this.graded;
    }

    public InventoryItem graded(Boolean graded) {
        this.setGraded(graded);
        return this;
    }

    public void setGraded(Boolean graded) {
        this.graded = graded;
    }

    public String getGrade() {
        return this.grade;
    }

    public InventoryItem grade(String grade) {
        this.setGrade(grade);
        return this;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public BigDecimal getPurchasePrice() {
        return this.purchasePrice;
    }

    public InventoryItem purchasePrice(BigDecimal purchasePrice) {
        this.setPurchasePrice(purchasePrice);
        return this;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public String getNotes() {
        return this.notes;
    }

    public InventoryItem notes(String notes) {
        this.setNotes(notes);
        return this;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Card getCard() {
        return this.card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public InventoryItem card(Card card) {
        this.setCard(card);
        return this;
    }

    public PokeUser getOwner() {
        return this.owner;
    }

    public void setOwner(PokeUser pokeUser) {
        this.owner = pokeUser;
    }

    public InventoryItem owner(PokeUser pokeUser) {
        this.setOwner(pokeUser);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof InventoryItem)) {
            return false;
        }
        return getId() != null && getId().equals(((InventoryItem) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "InventoryItem{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", condition='" + getCondition() + "'" +
            ", language='" + getLanguage() + "'" +
            ", graded='" + getGraded() + "'" +
            ", grade='" + getGrade() + "'" +
            ", purchasePrice=" + getPurchasePrice() +
            ", notes='" + getNotes() + "'" +
            "}";
    }
}
