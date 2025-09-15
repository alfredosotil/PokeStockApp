package com.poke.app.service;

import com.poke.app.domain.CardSet;
import com.poke.app.repository.CardSetRepository;
import com.poke.app.service.dto.CardSetDTO;
import com.poke.app.service.mapper.CardSetMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.poke.app.domain.CardSet}.
 */
@Service
@Transactional
public class CardSetService {

    private static final Logger LOG = LoggerFactory.getLogger(CardSetService.class);

    private final CardSetRepository cardSetRepository;

    private final CardSetMapper cardSetMapper;

    public CardSetService(CardSetRepository cardSetRepository, CardSetMapper cardSetMapper) {
        this.cardSetRepository = cardSetRepository;
        this.cardSetMapper = cardSetMapper;
    }

    /**
     * Save a cardSet.
     *
     * @param cardSetDTO the entity to save.
     * @return the persisted entity.
     */
    public CardSetDTO save(CardSetDTO cardSetDTO) {
        LOG.debug("Request to save CardSet : {}", cardSetDTO);
        CardSet cardSet = cardSetMapper.toEntity(cardSetDTO);
        cardSet = cardSetRepository.save(cardSet);
        return cardSetMapper.toDto(cardSet);
    }

    /**
     * Update a cardSet.
     *
     * @param cardSetDTO the entity to save.
     * @return the persisted entity.
     */
    public CardSetDTO update(CardSetDTO cardSetDTO) {
        LOG.debug("Request to update CardSet : {}", cardSetDTO);
        CardSet cardSet = cardSetMapper.toEntity(cardSetDTO);
        cardSet = cardSetRepository.save(cardSet);
        return cardSetMapper.toDto(cardSet);
    }

    /**
     * Partially update a cardSet.
     *
     * @param cardSetDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<CardSetDTO> partialUpdate(CardSetDTO cardSetDTO) {
        LOG.debug("Request to partially update CardSet : {}", cardSetDTO);

        return cardSetRepository
            .findById(cardSetDTO.getId())
            .map(existingCardSet -> {
                cardSetMapper.partialUpdate(existingCardSet, cardSetDTO);

                return existingCardSet;
            })
            .map(cardSetRepository::save)
            .map(cardSetMapper::toDto);
    }

    /**
     * Get all the cardSets.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CardSetDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all CardSets");
        return cardSetRepository.findAll(pageable).map(cardSetMapper::toDto);
    }

    /**
     * Get one cardSet by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CardSetDTO> findOne(Long id) {
        LOG.debug("Request to get CardSet : {}", id);
        return cardSetRepository.findById(id).map(cardSetMapper::toDto);
    }

    /**
     * Delete the cardSet by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete CardSet : {}", id);
        cardSetRepository.deleteById(id);
    }
}
