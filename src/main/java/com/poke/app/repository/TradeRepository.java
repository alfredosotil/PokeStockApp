package com.poke.app.repository;

import com.poke.app.domain.Trade;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Trade entity.
 */
@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    default Optional<Trade> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<Trade> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<Trade> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(value = "select trade from Trade trade left join fetch trade.proposer", countQuery = "select count(trade) from Trade trade")
    Page<Trade> findAllWithToOneRelationships(Pageable pageable);

    @Query("select trade from Trade trade left join fetch trade.proposer")
    List<Trade> findAllWithToOneRelationships();

    @Query("select trade from Trade trade left join fetch trade.proposer where trade.id =:id")
    Optional<Trade> findOneWithToOneRelationships(@Param("id") Long id);
}
