package com.poke.app.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.poke.app.IntegrationTest;
import com.poke.app.domain.Card;
import com.poke.app.domain.Trade;
import com.poke.app.domain.TradeItem;
import com.poke.app.domain.enumeration.TradeStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
class TradeItemRepositoryTest {

    @Autowired
    private TradeItemRepository tradeItemRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private EntityManagerFactory entityManagerFactory;

    @Test
    @Transactional
    void findOneWithToOneRelationshipsShouldLoadTradeAndCard() {
        Long tradeItemId = persistTradeItemGraph();
        em.clear();

        TradeItem tradeItem = tradeItemRepository.findOneWithToOneRelationships(tradeItemId).orElseThrow();

        assertTradeItemRelationsLoaded(tradeItem);
    }

    @Test
    @Transactional
    void findAllWithToOneRelationshipsShouldLoadTradeAndCard() {
        persistTradeItemGraph();
        em.clear();

        List<TradeItem> tradeItems = tradeItemRepository.findAllWithToOneRelationships();

        assertThat(tradeItems).isNotEmpty();
        tradeItems.forEach(this::assertTradeItemRelationsLoaded);
    }

    @Test
    @Transactional
    void findAllWithToOneRelationshipsPageShouldLoadTradeAndCard() {
        persistTradeItemGraph();
        em.clear();

        var page = tradeItemRepository.findAllWithToOneRelationships(PageRequest.of(0, 5));

        assertThat(page.getContent()).isNotEmpty();
        page.getContent().forEach(this::assertTradeItemRelationsLoaded);
    }

    private Long persistTradeItemGraph() {
        Trade trade = new Trade().status(TradeStatus.PROPOSED).message("message").createdAt(Instant.parse("2020-01-01T00:00:00Z"));
        em.persist(trade);

        Card card = new Card().tcgId(UUID.randomUUID().toString()).setCode("set-code").number("001").name("Sample Card");
        em.persist(card);

        TradeItem tradeItem = new TradeItem().quantity(1).side("BUY").trade(trade).card(card);
        em.persist(tradeItem);

        em.flush();
        return tradeItem.getId();
    }

    private void assertTradeItemRelationsLoaded(TradeItem tradeItem) {
        PersistenceUnitUtil persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
        assertThat(persistenceUnitUtil.isLoaded(tradeItem, "trade")).isTrue();
        assertThat(persistenceUnitUtil.isLoaded(tradeItem, "card")).isTrue();
        assertThat(tradeItem.getTrade()).isNotNull();
        assertThat(tradeItem.getCard()).isNotNull();
    }
}
