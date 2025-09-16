package com.poke.app.service.dto;

import com.poke.app.domain.User;
import java.io.Serial;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A DTO representing a user, with only the public attributes.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class UserDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    private String login;

    public UserDTO(User user) {
        this.id = user.getId();
        // Customize it here if you need, or not, firstName/lastName/etc
        this.login = user.getLogin();
    }

    @EqualsAndHashCode.Include
    private Object equalityIdentifier() {
        return id != null ? id : System.identityHashCode(this);
    }

    @EqualsAndHashCode.Include
    private Object loginEqualityIdentifier() {
        return id != null ? login : System.identityHashCode(this);
    }
}
