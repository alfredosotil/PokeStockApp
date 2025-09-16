package com.poke.app.service.dto;

import com.poke.app.domain.enumeration.TradeStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A DTO for the {@link com.poke.app.domain.Trade} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class TradeDTO implements Serializable {

    private Long id;

    @NotNull
    private TradeStatus status;

    private String message;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private PokeUserDTO proposer;

    @EqualsAndHashCode.Include
    private Object equalityIdentifier() {
        return id != null ? id : System.identityHashCode(this);
    }
}
