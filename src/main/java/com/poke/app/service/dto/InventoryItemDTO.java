package com.poke.app.service.dto;

import com.poke.app.domain.enumeration.CardCondition;
import com.poke.app.domain.enumeration.CardLanguage;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A DTO for the {@link com.poke.app.domain.InventoryItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class InventoryItemDTO implements Serializable {

    @EqualsAndHashCode.Include
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

    @EqualsAndHashCode.Include(replaces = "id")
    private IdEquality idEquality() {
        return IdEquality.of(this.id);
    }
}
