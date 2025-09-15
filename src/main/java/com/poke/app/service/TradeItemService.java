package com.poke.app.service;

import com.poke.app.domain.TradeItem;
import com.poke.app.repository.TradeItemRepository;
import com.poke.app.service.dto.TradeItemDTO;
import com.poke.app.service.mapper.TradeItemMapper;
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
 * Service Implementation for managing {@link com.poke.app.domain.TradeItem}.
 */
@Service
@Transactional
public class TradeItemService {

    private static final Logger LOG = LoggerFactory.getLogger(TradeItemService.class);

    private final TradeItemRepository tradeItemRepository;

    private final TradeItemMapper tradeItemMapper;

    public TradeItemService(TradeItemRepository tradeItemRepository, TradeItemMapper tradeItemMapper) {
        this.tradeItemRepository = tradeItemRepository;
        this.tradeItemMapper = tradeItemMapper;
    }

    /**
     * Save a tradeItem.
     *
     * @param tradeItemDTO the entity to save.
     * @return the persisted entity.
     */
    public TradeItemDTO save(TradeItemDTO tradeItemDTO) {
        LOG.debug("Request to save TradeItem : {}", tradeItemDTO);
        TradeItem tradeItem = tradeItemMapper.toEntity(tradeItemDTO);
        tradeItem = tradeItemRepository.save(tradeItem);
        return tradeItemMapper.toDto(tradeItem);
    }

    /**
     * Update a tradeItem.
     *
     * @param tradeItemDTO the entity to save.
     * @return the persisted entity.
     */
    public TradeItemDTO update(TradeItemDTO tradeItemDTO) {
        LOG.debug("Request to update TradeItem : {}", tradeItemDTO);
        TradeItem tradeItem = tradeItemMapper.toEntity(tradeItemDTO);
        tradeItem = tradeItemRepository.save(tradeItem);
        return tradeItemMapper.toDto(tradeItem);
    }

    /**
     * Partially update a tradeItem.
     *
     * @param tradeItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TradeItemDTO> partialUpdate(TradeItemDTO tradeItemDTO) {
        LOG.debug("Request to partially update TradeItem : {}", tradeItemDTO);

        return tradeItemRepository
            .findById(tradeItemDTO.getId())
            .map(existingTradeItem -> {
                tradeItemMapper.partialUpdate(existingTradeItem, tradeItemDTO);

                return existingTradeItem;
            })
            .map(tradeItemRepository::save)
            .map(tradeItemMapper::toDto);
    }

    /**
     * Get all the tradeItems.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TradeItemDTO> findAll() {
        LOG.debug("Request to get all TradeItems");
        return tradeItemRepository.findAll().stream().map(tradeItemMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the tradeItems with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TradeItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return tradeItemRepository.findAllWithEagerRelationships(pageable).map(tradeItemMapper::toDto);
    }

    /**
     * Get one tradeItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TradeItemDTO> findOne(Long id) {
        LOG.debug("Request to get TradeItem : {}", id);
        return tradeItemRepository.findOneWithEagerRelationships(id).map(tradeItemMapper::toDto);
    }

    /**
     * Delete the tradeItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete TradeItem : {}", id);
        tradeItemRepository.deleteById(id);
    }
}
