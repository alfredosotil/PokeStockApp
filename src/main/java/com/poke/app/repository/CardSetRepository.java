package com.poke.app.repository;

import com.poke.app.domain.CardSet;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the CardSet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CardSetRepository extends JpaRepository<CardSet, Long> {}
