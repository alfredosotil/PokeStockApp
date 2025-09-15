package com.poke.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.poke.app.domain.Card} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CardDTO implements Serializable {

    private Long id;

    @NotNull
    private String tcgId;

    @NotNull
    private String setCode;

    @NotNull
    private String number;

    @NotNull
    private String name;

    private String rarity;

    private String superType;

    private String types;

    private String imageUrl;

    private String legalities;

    private CardSetDTO set;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTcgId() {
        return tcgId;
    }

    public void setTcgId(String tcgId) {
        this.tcgId = tcgId;
    }

    public String getSetCode() {
        return setCode;
    }

    public void setSetCode(String setCode) {
        this.setCode = setCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getSuperType() {
        return superType;
    }

    public void setSuperType(String superType) {
        this.superType = superType;
    }

    public String getTypes() {
        return types;
    }

    public void setTypes(String types) {
        this.types = types;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLegalities() {
        return legalities;
    }

    public void setLegalities(String legalities) {
        this.legalities = legalities;
    }

    public CardSetDTO getSet() {
        return set;
    }

    public void setSet(CardSetDTO set) {
        this.set = set;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CardDTO)) {
            return false;
        }

        CardDTO cardDTO = (CardDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, cardDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CardDTO{" +
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
            ", set=" + getSet() +
            "}";
    }
}
