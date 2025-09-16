package com.poke.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A DTO for the {@link com.poke.app.domain.PokeUser} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PokeUserDTO implements Serializable {

    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    private String displayName;

    private String country;

    private UserDTO user;

    @EqualsAndHashCode.Include(replaces = "id")
    private IdEquality idEquality() {
        return IdEquality.of(this.id);
    }
}
