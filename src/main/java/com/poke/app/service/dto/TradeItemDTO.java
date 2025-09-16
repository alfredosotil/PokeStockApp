package com.poke.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A DTO for the {@link com.poke.app.domain.TradeItem} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TradeItemDTO implements Serializable {

    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    @Min(value = 1)
    private Integer quantity;

    @NotNull
    private String side;

    private TradeDTO trade;

    private CardDTO card;

    @EqualsAndHashCode.Include(replaces = "id")
    private IdEquality idEquality() {
        return IdEquality.of(this.id);
    }
}
