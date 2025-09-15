package com.poke.app.repository;

import com.poke.app.domain.MarketPrice;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the MarketPrice entity.
 */
@Repository
public interface MarketPriceRepository extends JpaRepository<MarketPrice, Long> {
    default Optional<MarketPrice> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<MarketPrice> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<MarketPrice> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select marketPrice from MarketPrice marketPrice left join fetch marketPrice.card",
        countQuery = "select count(marketPrice) from MarketPrice marketPrice"
    )
    Page<MarketPrice> findAllWithToOneRelationships(Pageable pageable);

    @Query("select marketPrice from MarketPrice marketPrice left join fetch marketPrice.card")
    List<MarketPrice> findAllWithToOneRelationships();

    @Query("select marketPrice from MarketPrice marketPrice left join fetch marketPrice.card where marketPrice.id =:id")
    Optional<MarketPrice> findOneWithToOneRelationships(@Param("id") Long id);
}
