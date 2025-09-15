package com.poke.app.web.rest;

import static com.poke.app.domain.CardSetAsserts.*;
import static com.poke.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poke.app.IntegrationTest;
import com.poke.app.domain.CardSet;
import com.poke.app.repository.CardSetRepository;
import com.poke.app.service.dto.CardSetDTO;
import com.poke.app.service.mapper.CardSetMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CardSetResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CardSetResourceIT {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Instant DEFAULT_RELEASE_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_RELEASE_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/card-sets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CardSetRepository cardSetRepository;

    @Autowired
    private CardSetMapper cardSetMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCardSetMockMvc;

    private CardSet cardSet;

    private CardSet insertedCardSet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CardSet createEntity() {
        return new CardSet().code(DEFAULT_CODE).name(DEFAULT_NAME).releaseDate(DEFAULT_RELEASE_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CardSet createUpdatedEntity() {
        return new CardSet().code(UPDATED_CODE).name(UPDATED_NAME).releaseDate(UPDATED_RELEASE_DATE);
    }

    @BeforeEach
    void initTest() {
        cardSet = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedCardSet != null) {
            cardSetRepository.delete(insertedCardSet);
            insertedCardSet = null;
        }
    }

    @Test
    @Transactional
    void createCardSet() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the CardSet
        CardSetDTO cardSetDTO = cardSetMapper.toDto(cardSet);
        var returnedCardSetDTO = om.readValue(
            restCardSetMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cardSetDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            CardSetDTO.class
        );

        // Validate the CardSet in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedCardSet = cardSetMapper.toEntity(returnedCardSetDTO);
        assertCardSetUpdatableFieldsEquals(returnedCardSet, getPersistedCardSet(returnedCardSet));

        insertedCardSet = returnedCardSet;
    }

    @Test
    @Transactional
    void createCardSetWithExistingId() throws Exception {
        // Create the CardSet with an existing ID
        cardSet.setId(1L);
        CardSetDTO cardSetDTO = cardSetMapper.toDto(cardSet);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCardSetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cardSetDTO)))
            .andExpect(status().isBadRequest());

        // Validate the CardSet in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCodeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cardSet.setCode(null);

        // Create the CardSet, which fails.
        CardSetDTO cardSetDTO = cardSetMapper.toDto(cardSet);

        restCardSetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cardSetDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        cardSet.setName(null);

        // Create the CardSet, which fails.
        CardSetDTO cardSetDTO = cardSetMapper.toDto(cardSet);

        restCardSetMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cardSetDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCardSets() throws Exception {
        // Initialize the database
        insertedCardSet = cardSetRepository.saveAndFlush(cardSet);

        // Get all the cardSetList
        restCardSetMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(cardSet.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE)))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].releaseDate").value(hasItem(DEFAULT_RELEASE_DATE.toString())));
    }

    @Test
    @Transactional
    void getCardSet() throws Exception {
        // Initialize the database
        insertedCardSet = cardSetRepository.saveAndFlush(cardSet);

        // Get the cardSet
        restCardSetMockMvc
            .perform(get(ENTITY_API_URL_ID, cardSet.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(cardSet.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.releaseDate").value(DEFAULT_RELEASE_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingCardSet() throws Exception {
        // Get the cardSet
        restCardSetMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingCardSet() throws Exception {
        // Initialize the database
        insertedCardSet = cardSetRepository.saveAndFlush(cardSet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cardSet
        CardSet updatedCardSet = cardSetRepository.findById(cardSet.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedCardSet are not directly saved in db
        em.detach(updatedCardSet);
        updatedCardSet.code(UPDATED_CODE).name(UPDATED_NAME).releaseDate(UPDATED_RELEASE_DATE);
        CardSetDTO cardSetDTO = cardSetMapper.toDto(updatedCardSet);

        restCardSetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cardSetDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cardSetDTO))
            )
            .andExpect(status().isOk());

        // Validate the CardSet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCardSetToMatchAllProperties(updatedCardSet);
    }

    @Test
    @Transactional
    void putNonExistingCardSet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cardSet.setId(longCount.incrementAndGet());

        // Create the CardSet
        CardSetDTO cardSetDTO = cardSetMapper.toDto(cardSet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCardSetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, cardSetDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cardSetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CardSet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCardSet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cardSet.setId(longCount.incrementAndGet());

        // Create the CardSet
        CardSetDTO cardSetDTO = cardSetMapper.toDto(cardSet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardSetMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(cardSetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CardSet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCardSet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cardSet.setId(longCount.incrementAndGet());

        // Create the CardSet
        CardSetDTO cardSetDTO = cardSetMapper.toDto(cardSet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardSetMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(cardSetDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CardSet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCardSetWithPatch() throws Exception {
        // Initialize the database
        insertedCardSet = cardSetRepository.saveAndFlush(cardSet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cardSet using partial update
        CardSet partialUpdatedCardSet = new CardSet();
        partialUpdatedCardSet.setId(cardSet.getId());

        partialUpdatedCardSet.name(UPDATED_NAME).releaseDate(UPDATED_RELEASE_DATE);

        restCardSetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCardSet.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCardSet))
            )
            .andExpect(status().isOk());

        // Validate the CardSet in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCardSetUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedCardSet, cardSet), getPersistedCardSet(cardSet));
    }

    @Test
    @Transactional
    void fullUpdateCardSetWithPatch() throws Exception {
        // Initialize the database
        insertedCardSet = cardSetRepository.saveAndFlush(cardSet);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the cardSet using partial update
        CardSet partialUpdatedCardSet = new CardSet();
        partialUpdatedCardSet.setId(cardSet.getId());

        partialUpdatedCardSet.code(UPDATED_CODE).name(UPDATED_NAME).releaseDate(UPDATED_RELEASE_DATE);

        restCardSetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCardSet.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedCardSet))
            )
            .andExpect(status().isOk());

        // Validate the CardSet in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCardSetUpdatableFieldsEquals(partialUpdatedCardSet, getPersistedCardSet(partialUpdatedCardSet));
    }

    @Test
    @Transactional
    void patchNonExistingCardSet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cardSet.setId(longCount.incrementAndGet());

        // Create the CardSet
        CardSetDTO cardSetDTO = cardSetMapper.toDto(cardSet);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCardSetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, cardSetDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cardSetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CardSet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCardSet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cardSet.setId(longCount.incrementAndGet());

        // Create the CardSet
        CardSetDTO cardSetDTO = cardSetMapper.toDto(cardSet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardSetMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(cardSetDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the CardSet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCardSet() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        cardSet.setId(longCount.incrementAndGet());

        // Create the CardSet
        CardSetDTO cardSetDTO = cardSetMapper.toDto(cardSet);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCardSetMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(cardSetDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the CardSet in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCardSet() throws Exception {
        // Initialize the database
        insertedCardSet = cardSetRepository.saveAndFlush(cardSet);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the cardSet
        restCardSetMockMvc
            .perform(delete(ENTITY_API_URL_ID, cardSet.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return cardSetRepository.count();
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

    protected CardSet getPersistedCardSet(CardSet cardSet) {
        return cardSetRepository.findById(cardSet.getId()).orElseThrow();
    }

    protected void assertPersistedCardSetToMatchAllProperties(CardSet expectedCardSet) {
        assertCardSetAllPropertiesEquals(expectedCardSet, getPersistedCardSet(expectedCardSet));
    }

    protected void assertPersistedCardSetToMatchUpdatableProperties(CardSet expectedCardSet) {
        assertCardSetAllUpdatablePropertiesEquals(expectedCardSet, getPersistedCardSet(expectedCardSet));
    }
}
