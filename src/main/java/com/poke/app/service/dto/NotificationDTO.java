package com.poke.app.service.dto;

import com.poke.app.domain.enumeration.NotificationType;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A DTO for the {@link com.poke.app.domain.Notification} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class NotificationDTO implements Serializable {

    @EqualsAndHashCode.Include
    private Long id;

    @NotNull
    private NotificationType type;

    @NotNull
    private String title;

    @Lob
    private String message;

    @NotNull
    private Boolean read;

    @NotNull
    private Instant createdAt;

    private String linkUrl;

    private UserDTO recipient;

    @EqualsAndHashCode.Include(replaces = "id")
    private IdEquality idEquality() {
        return IdEquality.of(this.id);
    }
}
