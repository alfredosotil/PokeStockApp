package com.poke.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A DTO for the {@link com.poke.app.domain.Card} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
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

    @EqualsAndHashCode.Include
    private Object equalityIdentifier() {
        return id != null ? id : System.identityHashCode(this);
    }
}
