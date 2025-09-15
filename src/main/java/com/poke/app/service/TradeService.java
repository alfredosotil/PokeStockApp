package com.poke.app.service;

import com.poke.app.domain.Trade;
import com.poke.app.repository.TradeRepository;
import com.poke.app.service.dto.TradeDTO;
import com.poke.app.service.mapper.TradeMapper;
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
 * Service Implementation for managing {@link com.poke.app.domain.Trade}.
 */
@Service
@Transactional
public class TradeService {

    private static final Logger LOG = LoggerFactory.getLogger(TradeService.class);

    private final TradeRepository tradeRepository;

    private final TradeMapper tradeMapper;

    public TradeService(TradeRepository tradeRepository, TradeMapper tradeMapper) {
        this.tradeRepository = tradeRepository;
        this.tradeMapper = tradeMapper;
    }

    /**
     * Save a trade.
     *
     * @param tradeDTO the entity to save.
     * @return the persisted entity.
     */
    public TradeDTO save(TradeDTO tradeDTO) {
        LOG.debug("Request to save Trade : {}", tradeDTO);
        Trade trade = tradeMapper.toEntity(tradeDTO);
        trade = tradeRepository.save(trade);
        return tradeMapper.toDto(trade);
    }

    /**
     * Update a trade.
     *
     * @param tradeDTO the entity to save.
     * @return the persisted entity.
     */
    public TradeDTO update(TradeDTO tradeDTO) {
        LOG.debug("Request to update Trade : {}", tradeDTO);
        Trade trade = tradeMapper.toEntity(tradeDTO);
        trade = tradeRepository.save(trade);
        return tradeMapper.toDto(trade);
    }

    /**
     * Partially update a trade.
     *
     * @param tradeDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<TradeDTO> partialUpdate(TradeDTO tradeDTO) {
        LOG.debug("Request to partially update Trade : {}", tradeDTO);

        return tradeRepository
            .findById(tradeDTO.getId())
            .map(existingTrade -> {
                tradeMapper.partialUpdate(existingTrade, tradeDTO);

                return existingTrade;
            })
            .map(tradeRepository::save)
            .map(tradeMapper::toDto);
    }

    /**
     * Get all the trades.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<TradeDTO> findAll() {
        LOG.debug("Request to get all Trades");
        return tradeRepository.findAll().stream().map(tradeMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get all the trades with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<TradeDTO> findAllWithEagerRelationships(Pageable pageable) {
        return tradeRepository.findAllWithEagerRelationships(pageable).map(tradeMapper::toDto);
    }

    /**
     * Get one trade by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<TradeDTO> findOne(Long id) {
        LOG.debug("Request to get Trade : {}", id);
        return tradeRepository.findOneWithEagerRelationships(id).map(tradeMapper::toDto);
    }

    /**
     * Delete the trade by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete Trade : {}", id);
        tradeRepository.deleteById(id);
    }
}
