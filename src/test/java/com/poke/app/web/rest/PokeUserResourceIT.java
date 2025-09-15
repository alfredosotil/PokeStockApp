package com.poke.app.web.rest;

import static com.poke.app.domain.PokeUserAsserts.*;
import static com.poke.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poke.app.IntegrationTest;
import com.poke.app.domain.PokeUser;
import com.poke.app.repository.PokeUserRepository;
import com.poke.app.repository.UserRepository;
import com.poke.app.service.PokeUserService;
import com.poke.app.service.dto.PokeUserDTO;
import com.poke.app.service.mapper.PokeUserMapper;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link PokeUserResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class PokeUserResourceIT {

    private static final String DEFAULT_DISPLAY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_DISPLAY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/poke-users";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private PokeUserRepository pokeUserRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private PokeUserRepository pokeUserRepositoryMock;

    @Autowired
    private PokeUserMapper pokeUserMapper;

    @Mock
    private PokeUserService pokeUserServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restPokeUserMockMvc;

    private PokeUser pokeUser;

    private PokeUser insertedPokeUser;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PokeUser createEntity() {
        return new PokeUser().displayName(DEFAULT_DISPLAY_NAME).country(DEFAULT_COUNTRY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PokeUser createUpdatedEntity() {
        return new PokeUser().displayName(UPDATED_DISPLAY_NAME).country(UPDATED_COUNTRY);
    }

    @BeforeEach
    void initTest() {
        pokeUser = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedPokeUser != null) {
            pokeUserRepository.delete(insertedPokeUser);
            insertedPokeUser = null;
        }
    }

    @Test
    @Transactional
    void createPokeUser() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the PokeUser
        PokeUserDTO pokeUserDTO = pokeUserMapper.toDto(pokeUser);
        var returnedPokeUserDTO = om.readValue(
            restPokeUserMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pokeUserDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            PokeUserDTO.class
        );

        // Validate the PokeUser in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedPokeUser = pokeUserMapper.toEntity(returnedPokeUserDTO);
        assertPokeUserUpdatableFieldsEquals(returnedPokeUser, getPersistedPokeUser(returnedPokeUser));

        insertedPokeUser = returnedPokeUser;
    }

    @Test
    @Transactional
    void createPokeUserWithExistingId() throws Exception {
        // Create the PokeUser with an existing ID
        pokeUser.setId(1L);
        PokeUserDTO pokeUserDTO = pokeUserMapper.toDto(pokeUser);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restPokeUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pokeUserDTO)))
            .andExpect(status().isBadRequest());

        // Validate the PokeUser in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDisplayNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        pokeUser.setDisplayName(null);

        // Create the PokeUser, which fails.
        PokeUserDTO pokeUserDTO = pokeUserMapper.toDto(pokeUser);

        restPokeUserMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pokeUserDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllPokeUsers() throws Exception {
        // Initialize the database
        insertedPokeUser = pokeUserRepository.saveAndFlush(pokeUser);

        // Get all the pokeUserList
        restPokeUserMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(pokeUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].displayName").value(hasItem(DEFAULT_DISPLAY_NAME)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPokeUsersWithEagerRelationshipsIsEnabled() throws Exception {
        when(pokeUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPokeUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(pokeUserServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPokeUsersWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(pokeUserServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restPokeUserMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(pokeUserRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getPokeUser() throws Exception {
        // Initialize the database
        insertedPokeUser = pokeUserRepository.saveAndFlush(pokeUser);

        // Get the pokeUser
        restPokeUserMockMvc
            .perform(get(ENTITY_API_URL_ID, pokeUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(pokeUser.getId().intValue()))
            .andExpect(jsonPath("$.displayName").value(DEFAULT_DISPLAY_NAME))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY));
    }

    @Test
    @Transactional
    void getNonExistingPokeUser() throws Exception {
        // Get the pokeUser
        restPokeUserMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingPokeUser() throws Exception {
        // Initialize the database
        insertedPokeUser = pokeUserRepository.saveAndFlush(pokeUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pokeUser
        PokeUser updatedPokeUser = pokeUserRepository.findById(pokeUser.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedPokeUser are not directly saved in db
        em.detach(updatedPokeUser);
        updatedPokeUser.displayName(UPDATED_DISPLAY_NAME).country(UPDATED_COUNTRY);
        PokeUserDTO pokeUserDTO = pokeUserMapper.toDto(updatedPokeUser);

        restPokeUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pokeUserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pokeUserDTO))
            )
            .andExpect(status().isOk());

        // Validate the PokeUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedPokeUserToMatchAllProperties(updatedPokeUser);
    }

    @Test
    @Transactional
    void putNonExistingPokeUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pokeUser.setId(longCount.incrementAndGet());

        // Create the PokeUser
        PokeUserDTO pokeUserDTO = pokeUserMapper.toDto(pokeUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPokeUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, pokeUserDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pokeUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PokeUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchPokeUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pokeUser.setId(longCount.incrementAndGet());

        // Create the PokeUser
        PokeUserDTO pokeUserDTO = pokeUserMapper.toDto(pokeUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPokeUserMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(pokeUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PokeUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamPokeUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pokeUser.setId(longCount.incrementAndGet());

        // Create the PokeUser
        PokeUserDTO pokeUserDTO = pokeUserMapper.toDto(pokeUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPokeUserMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(pokeUserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PokeUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdatePokeUserWithPatch() throws Exception {
        // Initialize the database
        insertedPokeUser = pokeUserRepository.saveAndFlush(pokeUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pokeUser using partial update
        PokeUser partialUpdatedPokeUser = new PokeUser();
        partialUpdatedPokeUser.setId(pokeUser.getId());

        partialUpdatedPokeUser.displayName(UPDATED_DISPLAY_NAME).country(UPDATED_COUNTRY);

        restPokeUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPokeUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPokeUser))
            )
            .andExpect(status().isOk());

        // Validate the PokeUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPokeUserUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedPokeUser, pokeUser), getPersistedPokeUser(pokeUser));
    }

    @Test
    @Transactional
    void fullUpdatePokeUserWithPatch() throws Exception {
        // Initialize the database
        insertedPokeUser = pokeUserRepository.saveAndFlush(pokeUser);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the pokeUser using partial update
        PokeUser partialUpdatedPokeUser = new PokeUser();
        partialUpdatedPokeUser.setId(pokeUser.getId());

        partialUpdatedPokeUser.displayName(UPDATED_DISPLAY_NAME).country(UPDATED_COUNTRY);

        restPokeUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedPokeUser.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedPokeUser))
            )
            .andExpect(status().isOk());

        // Validate the PokeUser in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPokeUserUpdatableFieldsEquals(partialUpdatedPokeUser, getPersistedPokeUser(partialUpdatedPokeUser));
    }

    @Test
    @Transactional
    void patchNonExistingPokeUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pokeUser.setId(longCount.incrementAndGet());

        // Create the PokeUser
        PokeUserDTO pokeUserDTO = pokeUserMapper.toDto(pokeUser);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restPokeUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, pokeUserDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pokeUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PokeUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchPokeUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pokeUser.setId(longCount.incrementAndGet());

        // Create the PokeUser
        PokeUserDTO pokeUserDTO = pokeUserMapper.toDto(pokeUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPokeUserMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(pokeUserDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the PokeUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamPokeUser() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        pokeUser.setId(longCount.incrementAndGet());

        // Create the PokeUser
        PokeUserDTO pokeUserDTO = pokeUserMapper.toDto(pokeUser);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restPokeUserMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(pokeUserDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the PokeUser in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deletePokeUser() throws Exception {
        // Initialize the database
        insertedPokeUser = pokeUserRepository.saveAndFlush(pokeUser);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the pokeUser
        restPokeUserMockMvc
            .perform(delete(ENTITY_API_URL_ID, pokeUser.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return pokeUserRepository.count();
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

    protected PokeUser getPersistedPokeUser(PokeUser pokeUser) {
        return pokeUserRepository.findById(pokeUser.getId()).orElseThrow();
    }

    protected void assertPersistedPokeUserToMatchAllProperties(PokeUser expectedPokeUser) {
        assertPokeUserAllPropertiesEquals(expectedPokeUser, getPersistedPokeUser(expectedPokeUser));
    }

    protected void assertPersistedPokeUserToMatchUpdatableProperties(PokeUser expectedPokeUser) {
        assertPokeUserAllUpdatablePropertiesEquals(expectedPokeUser, getPersistedPokeUser(expectedPokeUser));
    }
}
