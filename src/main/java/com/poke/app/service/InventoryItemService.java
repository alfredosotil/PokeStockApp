package com.poke.app.service;

import com.poke.app.domain.InventoryItem;
import com.poke.app.repository.InventoryItemRepository;
import com.poke.app.service.dto.InventoryItemDTO;
import com.poke.app.service.mapper.InventoryItemMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.poke.app.domain.InventoryItem}.
 */
@Service
@Transactional
public class InventoryItemService {

    private static final Logger LOG = LoggerFactory.getLogger(InventoryItemService.class);

    private final InventoryItemRepository inventoryItemRepository;

    private final InventoryItemMapper inventoryItemMapper;

    public InventoryItemService(InventoryItemRepository inventoryItemRepository, InventoryItemMapper inventoryItemMapper) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.inventoryItemMapper = inventoryItemMapper;
    }

    /**
     * Save a inventoryItem.
     *
     * @param inventoryItemDTO the entity to save.
     * @return the persisted entity.
     */
    public InventoryItemDTO save(InventoryItemDTO inventoryItemDTO) {
        LOG.debug("Request to save InventoryItem : {}", inventoryItemDTO);
        InventoryItem inventoryItem = inventoryItemMapper.toEntity(inventoryItemDTO);
        inventoryItem = inventoryItemRepository.save(inventoryItem);
        return inventoryItemMapper.toDto(inventoryItem);
    }

    /**
     * Update a inventoryItem.
     *
     * @param inventoryItemDTO the entity to save.
     * @return the persisted entity.
     */
    public InventoryItemDTO update(InventoryItemDTO inventoryItemDTO) {
        LOG.debug("Request to update InventoryItem : {}", inventoryItemDTO);
        InventoryItem inventoryItem = inventoryItemMapper.toEntity(inventoryItemDTO);
        inventoryItem = inventoryItemRepository.save(inventoryItem);
        return inventoryItemMapper.toDto(inventoryItem);
    }

    /**
     * Partially update a inventoryItem.
     *
     * @param inventoryItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<InventoryItemDTO> partialUpdate(InventoryItemDTO inventoryItemDTO) {
        LOG.debug("Request to partially update InventoryItem : {}", inventoryItemDTO);

        return inventoryItemRepository
            .findById(inventoryItemDTO.getId())
            .map(existingInventoryItem -> {
                inventoryItemMapper.partialUpdate(existingInventoryItem, inventoryItemDTO);

                return existingInventoryItem;
            })
            .map(inventoryItemRepository::save)
            .map(inventoryItemMapper::toDto);
    }

    /**
     * Get all the inventoryItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<InventoryItemDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all InventoryItems");
        return inventoryItemRepository.findAll(pageable).map(inventoryItemMapper::toDto);
    }

    /**
     * Get all the inventoryItems with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<InventoryItemDTO> findAllWithEagerRelationships(Pageable pageable) {
        return inventoryItemRepository.findAllWithEagerRelationships(pageable).map(inventoryItemMapper::toDto);
    }

    /**
     * Get one inventoryItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<InventoryItemDTO> findOne(Long id) {
        LOG.debug("Request to get InventoryItem : {}", id);
        return inventoryItemRepository.findOneWithEagerRelationships(id).map(inventoryItemMapper::toDto);
    }

    /**
     * Delete the inventoryItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete InventoryItem : {}", id);
        inventoryItemRepository.deleteById(id);
    }
}
