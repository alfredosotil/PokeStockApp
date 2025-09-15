package com.poke.app.web.rest;

import static com.poke.app.domain.TradeAsserts.*;
import static com.poke.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poke.app.IntegrationTest;
import com.poke.app.domain.Trade;
import com.poke.app.domain.enumeration.TradeStatus;
import com.poke.app.repository.TradeRepository;
import com.poke.app.service.TradeService;
import com.poke.app.service.dto.TradeDTO;
import com.poke.app.service.mapper.TradeMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TradeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TradeResourceIT {

    private static final TradeStatus DEFAULT_STATUS = TradeStatus.PROPOSED;
    private static final TradeStatus UPDATED_STATUS = TradeStatus.ACCEPTED;

    private static final String DEFAULT_MESSAGE = "AAAAAAAAAA";
    private static final String UPDATED_MESSAGE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/trades";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TradeRepository tradeRepository;

    @Mock
    private TradeRepository tradeRepositoryMock;

    @Autowired
    private TradeMapper tradeMapper;

    @Mock
    private TradeService tradeServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTradeMockMvc;

    private Trade trade;

    private Trade insertedTrade;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Trade createEntity() {
        return new Trade().status(DEFAULT_STATUS).message(DEFAULT_MESSAGE).createdAt(DEFAULT_CREATED_AT).updatedAt(DEFAULT_UPDATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Trade createUpdatedEntity() {
        return new Trade().status(UPDATED_STATUS).message(UPDATED_MESSAGE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
    }

    @BeforeEach
    void initTest() {
        trade = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTrade != null) {
            tradeRepository.delete(insertedTrade);
            insertedTrade = null;
        }
    }

    @Test
    @Transactional
    void createTrade() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Trade
        TradeDTO tradeDTO = tradeMapper.toDto(trade);
        var returnedTradeDTO = om.readValue(
            restTradeMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tradeDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TradeDTO.class
        );

        // Validate the Trade in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTrade = tradeMapper.toEntity(returnedTradeDTO);
        assertTradeUpdatableFieldsEquals(returnedTrade, getPersistedTrade(returnedTrade));

        insertedTrade = returnedTrade;
    }

    @Test
    @Transactional
    void createTradeWithExistingId() throws Exception {
        // Create the Trade with an existing ID
        trade.setId(1L);
        TradeDTO tradeDTO = tradeMapper.toDto(trade);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTradeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tradeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Trade in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trade.setStatus(null);

        // Create the Trade, which fails.
        TradeDTO tradeDTO = tradeMapper.toDto(trade);

        restTradeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tradeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        trade.setCreatedAt(null);

        // Create the Trade, which fails.
        TradeDTO tradeDTO = tradeMapper.toDto(trade);

        restTradeMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tradeDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTrades() throws Exception {
        // Initialize the database
        insertedTrade = tradeRepository.saveAndFlush(trade);

        // Get all the tradeList
        restTradeMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trade.getId().intValue())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].message").value(hasItem(DEFAULT_MESSAGE)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTradesWithEagerRelationshipsIsEnabled() throws Exception {
        when(tradeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTradeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(tradeServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTradesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(tradeServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restTradeMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(tradeRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getTrade() throws Exception {
        // Initialize the database
        insertedTrade = tradeRepository.saveAndFlush(trade);

        // Get the trade
        restTradeMockMvc
            .perform(get(ENTITY_API_URL_ID, trade.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(trade.getId().intValue()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.message").value(DEFAULT_MESSAGE))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()));
    }

    @Test
    @Transactional
    void getNonExistingTrade() throws Exception {
        // Get the trade
        restTradeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTrade() throws Exception {
        // Initialize the database
        insertedTrade = tradeRepository.saveAndFlush(trade);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trade
        Trade updatedTrade = tradeRepository.findById(trade.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTrade are not directly saved in db
        em.detach(updatedTrade);
        updatedTrade.status(UPDATED_STATUS).message(UPDATED_MESSAGE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);
        TradeDTO tradeDTO = tradeMapper.toDto(updatedTrade);

        restTradeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tradeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tradeDTO))
            )
            .andExpect(status().isOk());

        // Validate the Trade in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTradeToMatchAllProperties(updatedTrade);
    }

    @Test
    @Transactional
    void putNonExistingTrade() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trade.setId(longCount.incrementAndGet());

        // Create the Trade
        TradeDTO tradeDTO = tradeMapper.toDto(trade);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTradeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tradeDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tradeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trade in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTrade() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trade.setId(longCount.incrementAndGet());

        // Create the Trade
        TradeDTO tradeDTO = tradeMapper.toDto(trade);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTradeMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tradeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trade in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTrade() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trade.setId(longCount.incrementAndGet());

        // Create the Trade
        TradeDTO tradeDTO = tradeMapper.toDto(trade);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTradeMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tradeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Trade in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTradeWithPatch() throws Exception {
        // Initialize the database
        insertedTrade = tradeRepository.saveAndFlush(trade);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trade using partial update
        Trade partialUpdatedTrade = new Trade();
        partialUpdatedTrade.setId(trade.getId());

        partialUpdatedTrade.message(UPDATED_MESSAGE).createdAt(UPDATED_CREATED_AT);

        restTradeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTrade.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTrade))
            )
            .andExpect(status().isOk());

        // Validate the Trade in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTradeUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTrade, trade), getPersistedTrade(trade));
    }

    @Test
    @Transactional
    void fullUpdateTradeWithPatch() throws Exception {
        // Initialize the database
        insertedTrade = tradeRepository.saveAndFlush(trade);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the trade using partial update
        Trade partialUpdatedTrade = new Trade();
        partialUpdatedTrade.setId(trade.getId());

        partialUpdatedTrade.status(UPDATED_STATUS).message(UPDATED_MESSAGE).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

        restTradeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTrade.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTrade))
            )
            .andExpect(status().isOk());

        // Validate the Trade in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTradeUpdatableFieldsEquals(partialUpdatedTrade, getPersistedTrade(partialUpdatedTrade));
    }

    @Test
    @Transactional
    void patchNonExistingTrade() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trade.setId(longCount.incrementAndGet());

        // Create the Trade
        TradeDTO tradeDTO = tradeMapper.toDto(trade);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTradeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tradeDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tradeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trade in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTrade() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trade.setId(longCount.incrementAndGet());

        // Create the Trade
        TradeDTO tradeDTO = tradeMapper.toDto(trade);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTradeMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tradeDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Trade in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTrade() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        trade.setId(longCount.incrementAndGet());

        // Create the Trade
        TradeDTO tradeDTO = tradeMapper.toDto(trade);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTradeMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tradeDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Trade in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTrade() throws Exception {
        // Initialize the database
        insertedTrade = tradeRepository.saveAndFlush(trade);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the trade
        restTradeMockMvc
            .perform(delete(ENTITY_API_URL_ID, trade.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tradeRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Trade getPersistedTrade(Trade trade) {
        return tradeRepository.findById(trade.getId()).orElseThrow();
    }

    protected void assertPersistedTradeToMatchAllProperties(Trade expectedTrade) {
        assertTradeAllPropertiesEquals(expectedTrade, getPersistedTrade(expectedTrade));
    }

    protected void assertPersistedTradeToMatchUpdatableProperties(Trade expectedTrade) {
        assertTradeAllUpdatablePropertiesEquals(expectedTrade, getPersistedTrade(expectedTrade));
    }
}
