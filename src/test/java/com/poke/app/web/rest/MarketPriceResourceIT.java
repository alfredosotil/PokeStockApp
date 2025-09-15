package com.poke.app.web.rest;

import static com.poke.app.domain.MarketPriceAsserts.*;
import static com.poke.app.web.rest.TestUtil.createUpdateProxyForBean;
import static com.poke.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poke.app.IntegrationTest;
import com.poke.app.domain.MarketPrice;
import com.poke.app.domain.enumeration.MarketSource;
import com.poke.app.repository.MarketPriceRepository;
import com.poke.app.service.MarketPriceService;
import com.poke.app.service.dto.MarketPriceDTO;
import com.poke.app.service.mapper.MarketPriceMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link MarketPriceResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class MarketPriceResourceIT {

    private static final MarketSource DEFAULT_SOURCE = MarketSource.POKEMON_TCG_API;
    private static final MarketSource UPDATED_SOURCE = MarketSource.TCGPLAYER;

    private static final String DEFAULT_CURRENCY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PRICE_LOW = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE_LOW = new BigDecimal(2);

    private static final BigDecimal DEFAULT_PRICE_MID = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE_MID = new BigDecimal(2);

    private static final BigDecimal DEFAULT_PRICE_HIGH = new BigDecimal(1);
    private static final BigDecimal UPDATED_PRICE_HIGH = new BigDecimal(2);

    private static final Instant DEFAULT_LAST_UPDATED = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_UPDATED = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/market-prices";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private MarketPriceRepository marketPriceRepository;

    @Mock
    private MarketPriceRepository marketPriceRepositoryMock;

    @Autowired
    private MarketPriceMapper marketPriceMapper;

    @Mock
    private MarketPriceService marketPriceServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restMarketPriceMockMvc;

    private MarketPrice marketPrice;

    private MarketPrice insertedMarketPrice;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MarketPrice createEntity() {
        return new MarketPrice()
            .source(DEFAULT_SOURCE)
            .currency(DEFAULT_CURRENCY)
            .priceLow(DEFAULT_PRICE_LOW)
            .priceMid(DEFAULT_PRICE_MID)
            .priceHigh(DEFAULT_PRICE_HIGH)
            .lastUpdated(DEFAULT_LAST_UPDATED);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MarketPrice createUpdatedEntity() {
        return new MarketPrice()
            .source(UPDATED_SOURCE)
            .currency(UPDATED_CURRENCY)
            .priceLow(UPDATED_PRICE_LOW)
            .priceMid(UPDATED_PRICE_MID)
            .priceHigh(UPDATED_PRICE_HIGH)
            .lastUpdated(UPDATED_LAST_UPDATED);
    }

    @BeforeEach
    void initTest() {
        marketPrice = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedMarketPrice != null) {
            marketPriceRepository.delete(insertedMarketPrice);
            insertedMarketPrice = null;
        }
    }

    @Test
    @Transactional
    void createMarketPrice() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the MarketPrice
        MarketPriceDTO marketPriceDTO = marketPriceMapper.toDto(marketPrice);
        var returnedMarketPriceDTO = om.readValue(
            restMarketPriceMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(marketPriceDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            MarketPriceDTO.class
        );

        // Validate the MarketPrice in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedMarketPrice = marketPriceMapper.toEntity(returnedMarketPriceDTO);
        assertMarketPriceUpdatableFieldsEquals(returnedMarketPrice, getPersistedMarketPrice(returnedMarketPrice));

        insertedMarketPrice = returnedMarketPrice;
    }

    @Test
    @Transactional
    void createMarketPriceWithExistingId() throws Exception {
        // Create the MarketPrice with an existing ID
        marketPrice.setId(1L);
        MarketPriceDTO marketPriceDTO = marketPriceMapper.toDto(marketPrice);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restMarketPriceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(marketPriceDTO)))
            .andExpect(status().isBadRequest());

        // Validate the MarketPrice in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkSourceIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        marketPrice.setSource(null);

        // Create the MarketPrice, which fails.
        MarketPriceDTO marketPriceDTO = marketPriceMapper.toDto(marketPrice);

        restMarketPriceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(marketPriceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrencyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        marketPrice.setCurrency(null);

        // Create the MarketPrice, which fails.
        MarketPriceDTO marketPriceDTO = marketPriceMapper.toDto(marketPrice);

        restMarketPriceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(marketPriceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastUpdatedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        marketPrice.setLastUpdated(null);

        // Create the MarketPrice, which fails.
        MarketPriceDTO marketPriceDTO = marketPriceMapper.toDto(marketPrice);

        restMarketPriceMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(marketPriceDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllMarketPrices() throws Exception {
        // Initialize the database
        insertedMarketPrice = marketPriceRepository.saveAndFlush(marketPrice);

        // Get all the marketPriceList
        restMarketPriceMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(marketPrice.getId().intValue())))
            .andExpect(jsonPath("$.[*].source").value(hasItem(DEFAULT_SOURCE.toString())))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY)))
            .andExpect(jsonPath("$.[*].priceLow").value(hasItem(sameNumber(DEFAULT_PRICE_LOW))))
            .andExpect(jsonPath("$.[*].priceMid").value(hasItem(sameNumber(DEFAULT_PRICE_MID))))
            .andExpect(jsonPath("$.[*].priceHigh").value(hasItem(sameNumber(DEFAULT_PRICE_HIGH))))
            .andExpect(jsonPath("$.[*].lastUpdated").value(hasItem(DEFAULT_LAST_UPDATED.toString())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMarketPricesWithEagerRelationshipsIsEnabled() throws Exception {
        when(marketPriceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMarketPriceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(marketPriceServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllMarketPricesWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(marketPriceServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restMarketPriceMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(marketPriceRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getMarketPrice() throws Exception {
        // Initialize the database
        insertedMarketPrice = marketPriceRepository.saveAndFlush(marketPrice);

        // Get the marketPrice
        restMarketPriceMockMvc
            .perform(get(ENTITY_API_URL_ID, marketPrice.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(marketPrice.getId().intValue()))
            .andExpect(jsonPath("$.source").value(DEFAULT_SOURCE.toString()))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY))
            .andExpect(jsonPath("$.priceLow").value(sameNumber(DEFAULT_PRICE_LOW)))
            .andExpect(jsonPath("$.priceMid").value(sameNumber(DEFAULT_PRICE_MID)))
            .andExpect(jsonPath("$.priceHigh").value(sameNumber(DEFAULT_PRICE_HIGH)))
            .andExpect(jsonPath("$.lastUpdated").value(DEFAULT_LAST_UPDATED.toString()));
    }

    @Test
    @Transactional
    void getNonExistingMarketPrice() throws Exception {
        // Get the marketPrice
        restMarketPriceMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingMarketPrice() throws Exception {
        // Initialize the database
        insertedMarketPrice = marketPriceRepository.saveAndFlush(marketPrice);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the marketPrice
        MarketPrice updatedMarketPrice = marketPriceRepository.findById(marketPrice.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedMarketPrice are not directly saved in db
        em.detach(updatedMarketPrice);
        updatedMarketPrice
            .source(UPDATED_SOURCE)
            .currency(UPDATED_CURRENCY)
            .priceLow(UPDATED_PRICE_LOW)
            .priceMid(UPDATED_PRICE_MID)
            .priceHigh(UPDATED_PRICE_HIGH)
            .lastUpdated(UPDATED_LAST_UPDATED);
        MarketPriceDTO marketPriceDTO = marketPriceMapper.toDto(updatedMarketPrice);

        restMarketPriceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, marketPriceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(marketPriceDTO))
            )
            .andExpect(status().isOk());

        // Validate the MarketPrice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedMarketPriceToMatchAllProperties(updatedMarketPrice);
    }

    @Test
    @Transactional
    void putNonExistingMarketPrice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        marketPrice.setId(longCount.incrementAndGet());

        // Create the MarketPrice
        MarketPriceDTO marketPriceDTO = marketPriceMapper.toDto(marketPrice);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMarketPriceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, marketPriceDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(marketPriceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MarketPrice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchMarketPrice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        marketPrice.setId(longCount.incrementAndGet());

        // Create the MarketPrice
        MarketPriceDTO marketPriceDTO = marketPriceMapper.toDto(marketPrice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMarketPriceMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(marketPriceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MarketPrice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamMarketPrice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        marketPrice.setId(longCount.incrementAndGet());

        // Create the MarketPrice
        MarketPriceDTO marketPriceDTO = marketPriceMapper.toDto(marketPrice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMarketPriceMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(marketPriceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MarketPrice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateMarketPriceWithPatch() throws Exception {
        // Initialize the database
        insertedMarketPrice = marketPriceRepository.saveAndFlush(marketPrice);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the marketPrice using partial update
        MarketPrice partialUpdatedMarketPrice = new MarketPrice();
        partialUpdatedMarketPrice.setId(marketPrice.getId());

        partialUpdatedMarketPrice.currency(UPDATED_CURRENCY).priceMid(UPDATED_PRICE_MID);

        restMarketPriceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMarketPrice.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMarketPrice))
            )
            .andExpect(status().isOk());

        // Validate the MarketPrice in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMarketPriceUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedMarketPrice, marketPrice),
            getPersistedMarketPrice(marketPrice)
        );
    }

    @Test
    @Transactional
    void fullUpdateMarketPriceWithPatch() throws Exception {
        // Initialize the database
        insertedMarketPrice = marketPriceRepository.saveAndFlush(marketPrice);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the marketPrice using partial update
        MarketPrice partialUpdatedMarketPrice = new MarketPrice();
        partialUpdatedMarketPrice.setId(marketPrice.getId());

        partialUpdatedMarketPrice
            .source(UPDATED_SOURCE)
            .currency(UPDATED_CURRENCY)
            .priceLow(UPDATED_PRICE_LOW)
            .priceMid(UPDATED_PRICE_MID)
            .priceHigh(UPDATED_PRICE_HIGH)
            .lastUpdated(UPDATED_LAST_UPDATED);

        restMarketPriceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedMarketPrice.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedMarketPrice))
            )
            .andExpect(status().isOk());

        // Validate the MarketPrice in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertMarketPriceUpdatableFieldsEquals(partialUpdatedMarketPrice, getPersistedMarketPrice(partialUpdatedMarketPrice));
    }

    @Test
    @Transactional
    void patchNonExistingMarketPrice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        marketPrice.setId(longCount.incrementAndGet());

        // Create the MarketPrice
        MarketPriceDTO marketPriceDTO = marketPriceMapper.toDto(marketPrice);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restMarketPriceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, marketPriceDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(marketPriceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MarketPrice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchMarketPrice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        marketPrice.setId(longCount.incrementAndGet());

        // Create the MarketPrice
        MarketPriceDTO marketPriceDTO = marketPriceMapper.toDto(marketPrice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMarketPriceMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(marketPriceDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the MarketPrice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamMarketPrice() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        marketPrice.setId(longCount.incrementAndGet());

        // Create the MarketPrice
        MarketPriceDTO marketPriceDTO = marketPriceMapper.toDto(marketPrice);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restMarketPriceMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(marketPriceDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the MarketPrice in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteMarketPrice() throws Exception {
        // Initialize the database
        insertedMarketPrice = marketPriceRepository.saveAndFlush(marketPrice);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the marketPrice
        restMarketPriceMockMvc
            .perform(delete(ENTITY_API_URL_ID, marketPrice.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return marketPriceRepository.count();
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

    protected MarketPrice getPersistedMarketPrice(MarketPrice marketPrice) {
        return marketPriceRepository.findById(marketPrice.getId()).orElseThrow();
    }

    protected void assertPersistedMarketPriceToMatchAllProperties(MarketPrice expectedMarketPrice) {
        assertMarketPriceAllPropertiesEquals(expectedMarketPrice, getPersistedMarketPrice(expectedMarketPrice));
    }

    protected void assertPersistedMarketPriceToMatchUpdatableProperties(MarketPrice expectedMarketPrice) {
        assertMarketPriceAllUpdatablePropertiesEquals(expectedMarketPrice, getPersistedMarketPrice(expectedMarketPrice));
    }
}
