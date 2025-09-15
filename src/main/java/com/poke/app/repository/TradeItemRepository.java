package com.poke.app.repository;

import com.poke.app.domain.TradeItem;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TradeItem entity.
 */
@Repository
public interface TradeItemRepository extends JpaRepository<TradeItem, Long> {
    default Optional<TradeItem> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<TradeItem> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<TradeItem> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select tradeItem from TradeItem tradeItem left join fetch tradeItem.card",
        countQuery = "select count(tradeItem) from TradeItem tradeItem"
    )
    Page<TradeItem> findAllWithToOneRelationships(Pageable pageable);

    @Query("select tradeItem from TradeItem tradeItem left join fetch tradeItem.card")
    List<TradeItem> findAllWithToOneRelationships();

    @Query("select tradeItem from TradeItem tradeItem left join fetch tradeItem.card where tradeItem.id =:id")
    Optional<TradeItem> findOneWithToOneRelationships(@Param("id") Long id);
}
