package com.poke.app.service.dto;

import com.poke.app.domain.enumeration.TradeStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.poke.app.domain.Trade} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TradeDTO implements Serializable {

    private Long id;

    @NotNull
    private TradeStatus status;

    private String message;

    @NotNull
    private Instant createdAt;

    private Instant updatedAt;

    private PokeUserDTO proposer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TradeStatus getStatus() {
        return status;
    }

    public void setStatus(TradeStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public PokeUserDTO getProposer() {
        return proposer;
    }

    public void setProposer(PokeUserDTO proposer) {
        this.proposer = proposer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TradeDTO)) {
            return false;
        }

        TradeDTO tradeDTO = (TradeDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, tradeDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TradeDTO{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", message='" + getMessage() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", proposer=" + getProposer() +
            "}";
    }
}
