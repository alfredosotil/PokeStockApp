package com.poke.app.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.poke.app.domain.PokeUser} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class PokeUserDTO implements Serializable {

    private Long id;

    @NotNull
    private String displayName;

    private String country;

    private UserDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PokeUserDTO)) {
            return false;
        }

        PokeUserDTO pokeUserDTO = (PokeUserDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, pokeUserDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "PokeUserDTO{" +
            "id=" + getId() +
            ", displayName='" + getDisplayName() + "'" +
            ", country='" + getCountry() + "'" +
            ", user=" + getUser() +
            "}";
    }
}
