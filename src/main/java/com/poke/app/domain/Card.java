package com.poke.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Card.
 */
@Entity
@Table(name = "card")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Card implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "tcg_id", nullable = false, unique = true)
    private String tcgId;

    @NotNull
    @Column(name = "set_code", nullable = false)
    private String setCode;

    @NotNull
    @Column(name = "number", nullable = false)
    private String number;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "rarity")
    private String rarity;

    @Column(name = "super_type")
    private String superType;

    @Column(name = "types")
    private String types;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "legalities")
    private String legalities;

    @ManyToOne(fetch = FetchType.LAZY)
    private CardSet set;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Card id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTcgId() {
        return this.tcgId;
    }

    public Card tcgId(String tcgId) {
        this.setTcgId(tcgId);
        return this;
    }

    public void setTcgId(String tcgId) {
        this.tcgId = tcgId;
    }

    public String getSetCode() {
        return this.setCode;
    }

    public Card setCode(String setCode) {
        this.setSetCode(setCode);
        return this;
    }

    public void setSetCode(String setCode) {
        this.setCode = setCode;
    }

    public String getNumber() {
        return this.number;
    }

    public Card number(String number) {
        this.setNumber(number);
        return this;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return this.name;
    }

    public Card name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRarity() {
        return this.rarity;
    }

    public Card rarity(String rarity) {
        this.setRarity(rarity);
        return this;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getSuperType() {
        return this.superType;
    }

    public Card superType(String superType) {
        this.setSuperType(superType);
        return this;
    }

    public void setSuperType(String superType) {
        this.superType = superType;
    }

    public String getTypes() {
        return this.types;
    }

    public Card types(String types) {
        this.setTypes(types);
        return this;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public Card imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLegalities() {
        return this.legalities;
    }

    public Card legalities(String legalities) {
        this.setLegalities(legalities);
        return this;
    }

    public void setLegalities(String legalities) {
        this.legalities = legalities;
    }

    public CardSet getSet() {
        return this.set;
    }

    public void setSet(CardSet cardSet) {
        this.set = cardSet;
    }

    public Card set(CardSet cardSet) {
        this.setSet(cardSet);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Card)) {
            return false;
        }
        return getId() != null && getId().equals(((Card) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Card{" +
            "id=" + getId() +
            ", tcgId='" + getTcgId() + "'" +
            ", setCode='" + getSetCode() + "'" +
            ", number='" + getNumber() + "'" +
            ", name='" + getName() + "'" +
            ", rarity='" + getRarity() + "'" +
            ", superType='" + getSuperType() + "'" +
            ", types='" + getTypes() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            ", legalities='" + getLegalities() + "'" +
            "}";
    }
}
