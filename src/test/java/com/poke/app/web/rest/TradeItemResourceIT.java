package com.poke.app.web.rest;

import static com.poke.app.domain.TradeItemAsserts.*;
import static com.poke.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poke.app.IntegrationTest;
import com.poke.app.domain.TradeItem;
import com.poke.app.repository.TradeItemRepository;
import com.poke.app.service.TradeItemService;
import com.poke.app.service.dto.TradeItemDTO;
import com.poke.app.service.mapper.TradeItemMapper;
import jakarta.persistence.EntityManager;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link TradeItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class TradeItemResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final String DEFAULT_SIDE = "AAAAAAAAAA";
    private static final String UPDATED_SIDE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/trade-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TradeItemRepository tradeItemRepository;

    @Autowired
    private TradeItemMapper tradeItemMapper;

    @SpyBean
    private TradeItemService tradeItemService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTradeItemMockMvc;

    private TradeItem tradeItem;

    private TradeItem insertedTradeItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TradeItem createEntity() {
        return new TradeItem().quantity(DEFAULT_QUANTITY).side(DEFAULT_SIDE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TradeItem createUpdatedEntity() {
        return new TradeItem().quantity(UPDATED_QUANTITY).side(UPDATED_SIDE);
    }

    @BeforeEach
    void initTest() {
        tradeItem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedTradeItem != null) {
            tradeItemRepository.delete(insertedTradeItem);
            insertedTradeItem = null;
        }
        Mockito.reset(tradeItemService);
    }

    @Test
    @Transactional
    void createTradeItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the TradeItem
        TradeItemDTO tradeItemDTO = tradeItemMapper.toDto(tradeItem);
        var returnedTradeItemDTO = om.readValue(
            restTradeItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tradeItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TradeItemDTO.class
        );

        // Validate the TradeItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTradeItem = tradeItemMapper.toEntity(returnedTradeItemDTO);
        assertTradeItemUpdatableFieldsEquals(returnedTradeItem, getPersistedTradeItem(returnedTradeItem));

        insertedTradeItem = returnedTradeItem;
    }

    @Test
    @Transactional
    void createTradeItemWithExistingId() throws Exception {
        // Create the TradeItem with an existing ID
        tradeItem.setId(1L);
        TradeItemDTO tradeItemDTO = tradeItemMapper.toDto(tradeItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTradeItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tradeItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TradeItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tradeItem.setQuantity(null);

        // Create the TradeItem, which fails.
        TradeItemDTO tradeItemDTO = tradeItemMapper.toDto(tradeItem);

        restTradeItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tradeItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSideIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tradeItem.setSide(null);

        // Create the TradeItem, which fails.
        TradeItemDTO tradeItemDTO = tradeItemMapper.toDto(tradeItem);

        restTradeItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tradeItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTradeItems() throws Exception {
        // Initialize the database
        insertedTradeItem = tradeItemRepository.saveAndFlush(tradeItem);

        // Get all the tradeItemList
        restTradeItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(tradeItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].side").value(hasItem(DEFAULT_SIDE)));
    }

    @Test
    void getAllTradeItemsWithEagerRelationshipsIsEnabled() throws Exception {
        doReturn(Collections.emptyList()).when(tradeItemService).findAllWithEagerRelationships();

        restTradeItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(tradeItemService, times(1)).findAllWithEagerRelationships();
        verify(tradeItemService, never()).findAll();
    }

    @Test
    void getAllTradeItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        doReturn(Collections.emptyList()).when(tradeItemService).findAll();

        restTradeItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());

        verify(tradeItemService, times(1)).findAll();
        verify(tradeItemService, never()).findAllWithEagerRelationships();
    }

    @Test
    @Transactional
    void getTradeItem() throws Exception {
        // Initialize the database
        insertedTradeItem = tradeItemRepository.saveAndFlush(tradeItem);

        // Get the tradeItem
        restTradeItemMockMvc
            .perform(get(ENTITY_API_URL_ID, tradeItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(tradeItem.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.side").value(DEFAULT_SIDE));
    }

    @Test
    @Transactional
    void getNonExistingTradeItem() throws Exception {
        // Get the tradeItem
        restTradeItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingTradeItem() throws Exception {
        // Initialize the database
        insertedTradeItem = tradeItemRepository.saveAndFlush(tradeItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tradeItem
        TradeItem updatedTradeItem = tradeItemRepository.findById(tradeItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedTradeItem are not directly saved in db
        em.detach(updatedTradeItem);
        updatedTradeItem.quantity(UPDATED_QUANTITY).side(UPDATED_SIDE);
        TradeItemDTO tradeItemDTO = tradeItemMapper.toDto(updatedTradeItem);

        restTradeItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tradeItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tradeItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the TradeItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTradeItemToMatchAllProperties(updatedTradeItem);
    }

    @Test
    @Transactional
    void putNonExistingTradeItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tradeItem.setId(longCount.incrementAndGet());

        // Create the TradeItem
        TradeItemDTO tradeItemDTO = tradeItemMapper.toDto(tradeItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTradeItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tradeItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tradeItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TradeItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchTradeItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tradeItem.setId(longCount.incrementAndGet());

        // Create the TradeItem
        TradeItemDTO tradeItemDTO = tradeItemMapper.toDto(tradeItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTradeItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tradeItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TradeItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamTradeItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tradeItem.setId(longCount.incrementAndGet());

        // Create the TradeItem
        TradeItemDTO tradeItemDTO = tradeItemMapper.toDto(tradeItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTradeItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tradeItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TradeItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTradeItemWithPatch() throws Exception {
        // Initialize the database
        insertedTradeItem = tradeItemRepository.saveAndFlush(tradeItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tradeItem using partial update
        TradeItem partialUpdatedTradeItem = new TradeItem();
        partialUpdatedTradeItem.setId(tradeItem.getId());

        restTradeItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTradeItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTradeItem))
            )
            .andExpect(status().isOk());

        // Validate the TradeItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTradeItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedTradeItem, tradeItem),
            getPersistedTradeItem(tradeItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateTradeItemWithPatch() throws Exception {
        // Initialize the database
        insertedTradeItem = tradeItemRepository.saveAndFlush(tradeItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tradeItem using partial update
        TradeItem partialUpdatedTradeItem = new TradeItem();
        partialUpdatedTradeItem.setId(tradeItem.getId());

        partialUpdatedTradeItem.quantity(UPDATED_QUANTITY).side(UPDATED_SIDE);

        restTradeItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedTradeItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedTradeItem))
            )
            .andExpect(status().isOk());

        // Validate the TradeItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTradeItemUpdatableFieldsEquals(partialUpdatedTradeItem, getPersistedTradeItem(partialUpdatedTradeItem));
    }

    @Test
    @Transactional
    void patchNonExistingTradeItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tradeItem.setId(longCount.incrementAndGet());

        // Create the TradeItem
        TradeItemDTO tradeItemDTO = tradeItemMapper.toDto(tradeItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTradeItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tradeItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tradeItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TradeItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchTradeItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tradeItem.setId(longCount.incrementAndGet());

        // Create the TradeItem
        TradeItemDTO tradeItemDTO = tradeItemMapper.toDto(tradeItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTradeItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tradeItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the TradeItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamTradeItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tradeItem.setId(longCount.incrementAndGet());

        // Create the TradeItem
        TradeItemDTO tradeItemDTO = tradeItemMapper.toDto(tradeItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTradeItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tradeItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the TradeItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteTradeItem() throws Exception {
        // Initialize the database
        insertedTradeItem = tradeItemRepository.saveAndFlush(tradeItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tradeItem
        restTradeItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, tradeItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tradeItemRepository.count();
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

    protected TradeItem getPersistedTradeItem(TradeItem tradeItem) {
        return tradeItemRepository.findById(tradeItem.getId()).orElseThrow();
    }

    protected void assertPersistedTradeItemToMatchAllProperties(TradeItem expectedTradeItem) {
        assertTradeItemAllPropertiesEquals(expectedTradeItem, getPersistedTradeItem(expectedTradeItem));
    }

    protected void assertPersistedTradeItemToMatchUpdatableProperties(TradeItem expectedTradeItem) {
        assertTradeItemAllUpdatablePropertiesEquals(expectedTradeItem, getPersistedTradeItem(expectedTradeItem));
    }
}
