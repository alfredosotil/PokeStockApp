package com.poke.app.web.rest;

import static com.poke.app.domain.InventoryItemAsserts.*;
import static com.poke.app.web.rest.TestUtil.createUpdateProxyForBean;
import static com.poke.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poke.app.IntegrationTest;
import com.poke.app.domain.InventoryItem;
import com.poke.app.domain.enumeration.CardCondition;
import com.poke.app.domain.enumeration.CardLanguage;
import com.poke.app.repository.InventoryItemRepository;
import com.poke.app.service.InventoryItemService;
import com.poke.app.service.dto.InventoryItemDTO;
import com.poke.app.service.mapper.InventoryItemMapper;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
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
 * Integration tests for the {@link InventoryItemResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class InventoryItemResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final CardCondition DEFAULT_CONDITION = CardCondition.MINT;
    private static final CardCondition UPDATED_CONDITION = CardCondition.NEAR_MINT;

    private static final CardLanguage DEFAULT_LANGUAGE = CardLanguage.EN;
    private static final CardLanguage UPDATED_LANGUAGE = CardLanguage.JP;

    private static final Boolean DEFAULT_GRADED = false;
    private static final Boolean UPDATED_GRADED = true;

    private static final String DEFAULT_GRADE = "AAAAAAAAAA";
    private static final String UPDATED_GRADE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_PURCHASE_PRICE = new BigDecimal(1);
    private static final BigDecimal UPDATED_PURCHASE_PRICE = new BigDecimal(2);

    private static final String DEFAULT_NOTES = "AAAAAAAAAA";
    private static final String UPDATED_NOTES = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/inventory-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @Mock
    private InventoryItemRepository inventoryItemRepositoryMock;

    @Autowired
    private InventoryItemMapper inventoryItemMapper;

    @Mock
    private InventoryItemService inventoryItemServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restInventoryItemMockMvc;

    private InventoryItem inventoryItem;

    private InventoryItem insertedInventoryItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InventoryItem createEntity() {
        return new InventoryItem()
            .quantity(DEFAULT_QUANTITY)
            .condition(DEFAULT_CONDITION)
            .language(DEFAULT_LANGUAGE)
            .graded(DEFAULT_GRADED)
            .grade(DEFAULT_GRADE)
            .purchasePrice(DEFAULT_PURCHASE_PRICE)
            .notes(DEFAULT_NOTES);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static InventoryItem createUpdatedEntity() {
        return new InventoryItem()
            .quantity(UPDATED_QUANTITY)
            .condition(UPDATED_CONDITION)
            .language(UPDATED_LANGUAGE)
            .graded(UPDATED_GRADED)
            .grade(UPDATED_GRADE)
            .purchasePrice(UPDATED_PURCHASE_PRICE)
            .notes(UPDATED_NOTES);
    }

    @BeforeEach
    void initTest() {
        inventoryItem = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedInventoryItem != null) {
            inventoryItemRepository.delete(insertedInventoryItem);
            insertedInventoryItem = null;
        }
    }

    @Test
    @Transactional
    void createInventoryItem() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the InventoryItem
        InventoryItemDTO inventoryItemDTO = inventoryItemMapper.toDto(inventoryItem);
        var returnedInventoryItemDTO = om.readValue(
            restInventoryItemMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryItemDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            InventoryItemDTO.class
        );

        // Validate the InventoryItem in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedInventoryItem = inventoryItemMapper.toEntity(returnedInventoryItemDTO);
        assertInventoryItemUpdatableFieldsEquals(returnedInventoryItem, getPersistedInventoryItem(returnedInventoryItem));

        insertedInventoryItem = returnedInventoryItem;
    }

    @Test
    @Transactional
    void createInventoryItemWithExistingId() throws Exception {
        // Create the InventoryItem with an existing ID
        inventoryItem.setId(1L);
        InventoryItemDTO inventoryItemDTO = inventoryItemMapper.toDto(inventoryItem);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restInventoryItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryItemDTO)))
            .andExpect(status().isBadRequest());

        // Validate the InventoryItem in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkQuantityIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryItem.setQuantity(null);

        // Create the InventoryItem, which fails.
        InventoryItemDTO inventoryItemDTO = inventoryItemMapper.toDto(inventoryItem);

        restInventoryItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkConditionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryItem.setCondition(null);

        // Create the InventoryItem, which fails.
        InventoryItemDTO inventoryItemDTO = inventoryItemMapper.toDto(inventoryItem);

        restInventoryItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLanguageIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryItem.setLanguage(null);

        // Create the InventoryItem, which fails.
        InventoryItemDTO inventoryItemDTO = inventoryItemMapper.toDto(inventoryItem);

        restInventoryItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGradedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        inventoryItem.setGraded(null);

        // Create the InventoryItem, which fails.
        InventoryItemDTO inventoryItemDTO = inventoryItemMapper.toDto(inventoryItem);

        restInventoryItemMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryItemDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllInventoryItems() throws Exception {
        // Initialize the database
        insertedInventoryItem = inventoryItemRepository.saveAndFlush(inventoryItem);

        // Get all the inventoryItemList
        restInventoryItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(inventoryItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)))
            .andExpect(jsonPath("$.[*].condition").value(hasItem(DEFAULT_CONDITION.toString())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())))
            .andExpect(jsonPath("$.[*].graded").value(hasItem(DEFAULT_GRADED)))
            .andExpect(jsonPath("$.[*].grade").value(hasItem(DEFAULT_GRADE)))
            .andExpect(jsonPath("$.[*].purchasePrice").value(hasItem(sameNumber(DEFAULT_PURCHASE_PRICE))))
            .andExpect(jsonPath("$.[*].notes").value(hasItem(DEFAULT_NOTES)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInventoryItemsWithEagerRelationshipsIsEnabled() throws Exception {
        when(inventoryItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInventoryItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(inventoryItemServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllInventoryItemsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(inventoryItemServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restInventoryItemMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(inventoryItemRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getInventoryItem() throws Exception {
        // Initialize the database
        insertedInventoryItem = inventoryItemRepository.saveAndFlush(inventoryItem);

        // Get the inventoryItem
        restInventoryItemMockMvc
            .perform(get(ENTITY_API_URL_ID, inventoryItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(inventoryItem.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY))
            .andExpect(jsonPath("$.condition").value(DEFAULT_CONDITION.toString()))
            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()))
            .andExpect(jsonPath("$.graded").value(DEFAULT_GRADED))
            .andExpect(jsonPath("$.grade").value(DEFAULT_GRADE))
            .andExpect(jsonPath("$.purchasePrice").value(sameNumber(DEFAULT_PURCHASE_PRICE)))
            .andExpect(jsonPath("$.notes").value(DEFAULT_NOTES));
    }

    @Test
    @Transactional
    void getNonExistingInventoryItem() throws Exception {
        // Get the inventoryItem
        restInventoryItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingInventoryItem() throws Exception {
        // Initialize the database
        insertedInventoryItem = inventoryItemRepository.saveAndFlush(inventoryItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventoryItem
        InventoryItem updatedInventoryItem = inventoryItemRepository.findById(inventoryItem.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedInventoryItem are not directly saved in db
        em.detach(updatedInventoryItem);
        updatedInventoryItem
            .quantity(UPDATED_QUANTITY)
            .condition(UPDATED_CONDITION)
            .language(UPDATED_LANGUAGE)
            .graded(UPDATED_GRADED)
            .grade(UPDATED_GRADE)
            .purchasePrice(UPDATED_PURCHASE_PRICE)
            .notes(UPDATED_NOTES);
        InventoryItemDTO inventoryItemDTO = inventoryItemMapper.toDto(updatedInventoryItem);

        restInventoryItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventoryItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventoryItemDTO))
            )
            .andExpect(status().isOk());

        // Validate the InventoryItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedInventoryItemToMatchAllProperties(updatedInventoryItem);
    }

    @Test
    @Transactional
    void putNonExistingInventoryItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryItem.setId(longCount.incrementAndGet());

        // Create the InventoryItem
        InventoryItemDTO inventoryItemDTO = inventoryItemMapper.toDto(inventoryItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, inventoryItemDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventoryItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchInventoryItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryItem.setId(longCount.incrementAndGet());

        // Create the InventoryItem
        InventoryItemDTO inventoryItemDTO = inventoryItemMapper.toDto(inventoryItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(inventoryItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamInventoryItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryItem.setId(longCount.incrementAndGet());

        // Create the InventoryItem
        InventoryItemDTO inventoryItemDTO = inventoryItemMapper.toDto(inventoryItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryItemMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(inventoryItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InventoryItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateInventoryItemWithPatch() throws Exception {
        // Initialize the database
        insertedInventoryItem = inventoryItemRepository.saveAndFlush(inventoryItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventoryItem using partial update
        InventoryItem partialUpdatedInventoryItem = new InventoryItem();
        partialUpdatedInventoryItem.setId(inventoryItem.getId());

        partialUpdatedInventoryItem
            .quantity(UPDATED_QUANTITY)
            .language(UPDATED_LANGUAGE)
            .grade(UPDATED_GRADE)
            .purchasePrice(UPDATED_PURCHASE_PRICE);

        restInventoryItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventoryItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInventoryItem))
            )
            .andExpect(status().isOk());

        // Validate the InventoryItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventoryItemUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedInventoryItem, inventoryItem),
            getPersistedInventoryItem(inventoryItem)
        );
    }

    @Test
    @Transactional
    void fullUpdateInventoryItemWithPatch() throws Exception {
        // Initialize the database
        insertedInventoryItem = inventoryItemRepository.saveAndFlush(inventoryItem);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the inventoryItem using partial update
        InventoryItem partialUpdatedInventoryItem = new InventoryItem();
        partialUpdatedInventoryItem.setId(inventoryItem.getId());

        partialUpdatedInventoryItem
            .quantity(UPDATED_QUANTITY)
            .condition(UPDATED_CONDITION)
            .language(UPDATED_LANGUAGE)
            .graded(UPDATED_GRADED)
            .grade(UPDATED_GRADE)
            .purchasePrice(UPDATED_PURCHASE_PRICE)
            .notes(UPDATED_NOTES);

        restInventoryItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedInventoryItem.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedInventoryItem))
            )
            .andExpect(status().isOk());

        // Validate the InventoryItem in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertInventoryItemUpdatableFieldsEquals(partialUpdatedInventoryItem, getPersistedInventoryItem(partialUpdatedInventoryItem));
    }

    @Test
    @Transactional
    void patchNonExistingInventoryItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryItem.setId(longCount.incrementAndGet());

        // Create the InventoryItem
        InventoryItemDTO inventoryItemDTO = inventoryItemMapper.toDto(inventoryItem);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restInventoryItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, inventoryItemDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inventoryItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchInventoryItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryItem.setId(longCount.incrementAndGet());

        // Create the InventoryItem
        InventoryItemDTO inventoryItemDTO = inventoryItemMapper.toDto(inventoryItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(inventoryItemDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the InventoryItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamInventoryItem() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        inventoryItem.setId(longCount.incrementAndGet());

        // Create the InventoryItem
        InventoryItemDTO inventoryItemDTO = inventoryItemMapper.toDto(inventoryItem);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restInventoryItemMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(inventoryItemDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the InventoryItem in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteInventoryItem() throws Exception {
        // Initialize the database
        insertedInventoryItem = inventoryItemRepository.saveAndFlush(inventoryItem);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the inventoryItem
        restInventoryItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, inventoryItem.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return inventoryItemRepository.count();
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

    protected InventoryItem getPersistedInventoryItem(InventoryItem inventoryItem) {
        return inventoryItemRepository.findById(inventoryItem.getId()).orElseThrow();
    }

    protected void assertPersistedInventoryItemToMatchAllProperties(InventoryItem expectedInventoryItem) {
        assertInventoryItemAllPropertiesEquals(expectedInventoryItem, getPersistedInventoryItem(expectedInventoryItem));
    }

    protected void assertPersistedInventoryItemToMatchUpdatableProperties(InventoryItem expectedInventoryItem) {
        assertInventoryItemAllUpdatablePropertiesEquals(expectedInventoryItem, getPersistedInventoryItem(expectedInventoryItem));
    }
}
