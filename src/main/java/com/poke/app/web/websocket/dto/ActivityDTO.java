package com.poke.app.web.websocket.dto;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO for storing a user's activity.
 */
@Getter
@Setter
@ToString
public class ActivityDTO {

    private String sessionId;

    private String userLogin;

    private String ipAddress;

    private String page;

    private Instant time;
}
