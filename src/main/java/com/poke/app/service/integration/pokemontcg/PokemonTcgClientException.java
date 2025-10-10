package com.poke.app.service.integration.pokemontcg;

public class PokemonTcgClientException extends RuntimeException {

    public PokemonTcgClientException(String message) {
        super(message);
    }

    public PokemonTcgClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
