package com.poke.app.service;

import com.poke.app.domain.PokeUser;
import com.poke.app.repository.PokeUserRepository;
import com.poke.app.service.dto.PokeUserDTO;
import com.poke.app.service.mapper.PokeUserMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.poke.app.domain.PokeUser}.
 */
@Service
@Transactional
public class PokeUserService {

    private static final Logger LOG = LoggerFactory.getLogger(PokeUserService.class);

    private final PokeUserRepository pokeUserRepository;

    private final PokeUserMapper pokeUserMapper;

    public PokeUserService(PokeUserRepository pokeUserRepository, PokeUserMapper pokeUserMapper) {
        this.pokeUserRepository = pokeUserRepository;
        this.pokeUserMapper = pokeUserMapper;
    }

    /**
     * Save a pokeUser.
     *
     * @param pokeUserDTO the entity to save.
     * @return the persisted entity.
     */
    public PokeUserDTO save(PokeUserDTO pokeUserDTO) {
        LOG.debug("Request to save PokeUser : {}", pokeUserDTO);
        PokeUser pokeUser = pokeUserMapper.toEntity(pokeUserDTO);
        pokeUser = pokeUserRepository.save(pokeUser);
        return pokeUserMapper.toDto(pokeUser);
    }

    /**
     * Update a pokeUser.
     *
     * @param pokeUserDTO the entity to save.
     * @return the persisted entity.
     */
    public PokeUserDTO update(PokeUserDTO pokeUserDTO) {
        LOG.debug("Request to update PokeUser : {}", pokeUserDTO);
        PokeUser pokeUser = pokeUserMapper.toEntity(pokeUserDTO);
        pokeUser = pokeUserRepository.save(pokeUser);
        return pokeUserMapper.toDto(pokeUser);
    }

    /**
     * Partially update a pokeUser.
     *
     * @param pokeUserDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<PokeUserDTO> partialUpdate(PokeUserDTO pokeUserDTO) {
        LOG.debug("Request to partially update PokeUser : {}", pokeUserDTO);

        return pokeUserRepository
            .findById(pokeUserDTO.getId())
            .map(existingPokeUser -> {
                pokeUserMapper.partialUpdate(existingPokeUser, pokeUserDTO);

                return existingPokeUser;
            })
            .map(pokeUserRepository::save)
            .map(pokeUserMapper::toDto);
    }

    /**
     * Get all the pokeUsers.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<PokeUserDTO> findAll() {
        LOG.debug("Request to get all PokeUsers");
        return pokeUserRepository.findAll().stream().map(pokeUserMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the pokeUsers with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<PokeUserDTO> findAllWithEagerRelationships(Pageable pageable) {
        return pokeUserRepository.findAllWithEagerRelationships(pageable).map(pokeUserMapper::toDto);
    }

    /**
     * Get one pokeUser by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<PokeUserDTO> findOne(Long id) {
        LOG.debug("Request to get PokeUser : {}", id);
        return pokeUserRepository.findOneWithEagerRelationships(id).map(pokeUserMapper::toDto);
    }

    /**
     * Delete the pokeUser by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete PokeUser : {}", id);
        pokeUserRepository.deleteById(id);
    }
}
