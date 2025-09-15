package com.poke.app.web.rest;

import com.poke.app.repository.MarketPriceRepository;
import com.poke.app.service.MarketPriceService;
import com.poke.app.service.dto.MarketPriceDTO;
import com.poke.app.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.poke.app.domain.MarketPrice}.
 */
@RestController
@RequestMapping("/api/market-prices")
public class MarketPriceResource {

    private static final Logger LOG = LoggerFactory.getLogger(MarketPriceResource.class);

    private static final String ENTITY_NAME = "marketPrice";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MarketPriceService marketPriceService;

    private final MarketPriceRepository marketPriceRepository;

    public MarketPriceResource(MarketPriceService marketPriceService, MarketPriceRepository marketPriceRepository) {
        this.marketPriceService = marketPriceService;
        this.marketPriceRepository = marketPriceRepository;
    }

    /**
     * {@code POST  /market-prices} : Create a new marketPrice.
     *
     * @param marketPriceDTO the marketPriceDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new marketPriceDTO, or with status {@code 400 (Bad Request)} if the marketPrice has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<MarketPriceDTO> createMarketPrice(@Valid @RequestBody MarketPriceDTO marketPriceDTO) throws URISyntaxException {
        LOG.debug("REST request to save MarketPrice : {}", marketPriceDTO);
        if (marketPriceDTO.getId() != null) {
            throw new BadRequestAlertException("A new marketPrice cannot already have an ID", ENTITY_NAME, "idexists");
        }
        marketPriceDTO = marketPriceService.save(marketPriceDTO);
        return ResponseEntity.created(new URI("/api/market-prices/" + marketPriceDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, marketPriceDTO.getId().toString()))
            .body(marketPriceDTO);
    }

    /**
     * {@code PUT  /market-prices/:id} : Updates an existing marketPrice.
     *
     * @param id the id of the marketPriceDTO to save.
     * @param marketPriceDTO the marketPriceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated marketPriceDTO,
     * or with status {@code 400 (Bad Request)} if the marketPriceDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the marketPriceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<MarketPriceDTO> updateMarketPrice(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody MarketPriceDTO marketPriceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update MarketPrice : {}, {}", id, marketPriceDTO);
        if (marketPriceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, marketPriceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!marketPriceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        marketPriceDTO = marketPriceService.update(marketPriceDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, marketPriceDTO.getId().toString()))
            .body(marketPriceDTO);
    }

    /**
     * {@code PATCH  /market-prices/:id} : Partial updates given fields of an existing marketPrice, field will ignore if it is null
     *
     * @param id the id of the marketPriceDTO to save.
     * @param marketPriceDTO the marketPriceDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated marketPriceDTO,
     * or with status {@code 400 (Bad Request)} if the marketPriceDTO is not valid,
     * or with status {@code 404 (Not Found)} if the marketPriceDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the marketPriceDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<MarketPriceDTO> partialUpdateMarketPrice(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody MarketPriceDTO marketPriceDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update MarketPrice partially : {}, {}", id, marketPriceDTO);
        if (marketPriceDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, marketPriceDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!marketPriceRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<MarketPriceDTO> result = marketPriceService.partialUpdate(marketPriceDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, marketPriceDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /market-prices} : get all the marketPrices.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of marketPrices in body.
     */
    @GetMapping("")
    public ResponseEntity<List<MarketPriceDTO>> getAllMarketPrices(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of MarketPrices");
        Page<MarketPriceDTO> page;
        if (eagerload) {
            page = marketPriceService.findAllWithEagerRelationships(pageable);
        } else {
            page = marketPriceService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /market-prices/:id} : get the "id" marketPrice.
     *
     * @param id the id of the marketPriceDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the marketPriceDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<MarketPriceDTO> getMarketPrice(@PathVariable("id") Long id) {
        LOG.debug("REST request to get MarketPrice : {}", id);
        Optional<MarketPriceDTO> marketPriceDTO = marketPriceService.findOne(id);
        return ResponseUtil.wrapOrNotFound(marketPriceDTO);
    }

    /**
     * {@code DELETE  /market-prices/:id} : delete the "id" marketPrice.
     *
     * @param id the id of the marketPriceDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMarketPrice(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete MarketPrice : {}", id);
        marketPriceService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
