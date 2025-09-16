package com.poke.app.service.dto;

import java.util.Objects;

final class IdEquality {

    private final Long id;

    private IdEquality(Long id) {
        this.id = id;
    }

    static IdEquality of(Long id) {
        return new IdEquality(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IdEquality other)) {
            return false;
        }
        if (this.id == null || other.id == null) {
            return false;
        }
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}
