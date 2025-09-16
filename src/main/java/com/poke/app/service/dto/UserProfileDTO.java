package com.poke.app.service.dto;

import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A DTO for the {@link com.poke.app.domain.UserProfile} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserProfileDTO implements Serializable {

    private Long id;

    @Size(max = 1000)
    private String bio;

    private String location;

    private String favoriteSet;

    private String playstyle;

    @Lob
    private byte[] avatar;

    private String avatarContentType;

    private UserDTO user;

    @EqualsAndHashCode.Include
    private Object equalityIdentifier() {
        return id != null ? id : System.identityHashCode(this);
    }
}
