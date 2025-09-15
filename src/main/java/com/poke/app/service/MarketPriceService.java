package com.poke.app.service;

import com.poke.app.domain.MarketPrice;
import com.poke.app.repository.MarketPriceRepository;
import com.poke.app.service.dto.MarketPriceDTO;
import com.poke.app.service.mapper.MarketPriceMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.poke.app.domain.MarketPrice}.
 */
@Service
@Transactional
public class MarketPriceService {

    private static final Logger LOG = LoggerFactory.getLogger(MarketPriceService.class);

    private final MarketPriceRepository marketPriceRepository;

    private final MarketPriceMapper marketPriceMapper;

    public MarketPriceService(MarketPriceRepository marketPriceRepository, MarketPriceMapper marketPriceMapper) {
        this.marketPriceRepository = marketPriceRepository;
        this.marketPriceMapper = marketPriceMapper;
    }

    /**
     * Save a marketPrice.
     *
     * @param marketPriceDTO the entity to save.
     * @return the persisted entity.
     */
    public MarketPriceDTO save(MarketPriceDTO marketPriceDTO) {
        LOG.debug("Request to save MarketPrice : {}", marketPriceDTO);
        MarketPrice marketPrice = marketPriceMapper.toEntity(marketPriceDTO);
        marketPrice = marketPriceRepository.save(marketPrice);
        return marketPriceMapper.toDto(marketPrice);
    }

    /**
     * Update a marketPrice.
     *
     * @param marketPriceDTO the entity to save.
     * @return the persisted entity.
     */
    public MarketPriceDTO update(MarketPriceDTO marketPriceDTO) {
        LOG.debug("Request to update MarketPrice : {}", marketPriceDTO);
        MarketPrice marketPrice = marketPriceMapper.toEntity(marketPriceDTO);
        marketPrice = marketPriceRepository.save(marketPrice);
        return marketPriceMapper.toDto(marketPrice);
    }

    /**
     * Partially update a marketPrice.
     *
     * @param marketPriceDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<MarketPriceDTO> partialUpdate(MarketPriceDTO marketPriceDTO) {
        LOG.debug("Request to partially update MarketPrice : {}", marketPriceDTO);

        return marketPriceRepository
            .findById(marketPriceDTO.getId())
            .map(existingMarketPrice -> {
                marketPriceMapper.partialUpdate(existingMarketPrice, marketPriceDTO);

                return existingMarketPrice;
            })
            .map(marketPriceRepository::save)
            .map(marketPriceMapper::toDto);
    }

    /**
     * Get all the marketPrices.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<MarketPriceDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all MarketPrices");
        return marketPriceRepository.findAll(pageable).map(marketPriceMapper::toDto);
    }

    /**
     * Get all the marketPrices with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<MarketPriceDTO> findAllWithEagerRelationships(Pageable pageable) {
        return marketPriceRepository.findAllWithEagerRelationships(pageable).map(marketPriceMapper::toDto);
    }

    /**
     * Get one marketPrice by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<MarketPriceDTO> findOne(Long id) {
        LOG.debug("Request to get MarketPrice : {}", id);
        return marketPriceRepository.findOneWithEagerRelationships(id).map(marketPriceMapper::toDto);
    }

    /**
     * Delete the marketPrice by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete MarketPrice : {}", id);
        marketPriceRepository.deleteById(id);
    }
}
