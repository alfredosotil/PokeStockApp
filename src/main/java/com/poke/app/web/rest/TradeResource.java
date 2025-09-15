package com.poke.app.web.rest;

import com.poke.app.repository.TradeRepository;
import com.poke.app.service.TradeService;
import com.poke.app.service.dto.TradeDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.poke.app.domain.Trade}.
 */
@RestController
@RequestMapping("/api/trades")
public class TradeResource {

    private static final Logger LOG = LoggerFactory.getLogger(TradeResource.class);

    private static final String ENTITY_NAME = "trade";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TradeService tradeService;

    private final TradeRepository tradeRepository;

    public TradeResource(TradeService tradeService, TradeRepository tradeRepository) {
        this.tradeService = tradeService;
        this.tradeRepository = tradeRepository;
    }

    /**
     * {@code POST  /trades} : Create a new trade.
     *
     * @param tradeDTO the tradeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tradeDTO, or with status {@code 400 (Bad Request)} if the trade has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TradeDTO> createTrade(@Valid @RequestBody TradeDTO tradeDTO) throws URISyntaxException {
        LOG.debug("REST request to save Trade : {}", tradeDTO);
        if (tradeDTO.getId() != null) {
            throw new BadRequestAlertException("A new trade cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tradeDTO = tradeService.save(tradeDTO);
        return ResponseEntity.created(new URI("/api/trades/" + tradeDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, tradeDTO.getId().toString()))
            .body(tradeDTO);
    }

    /**
     * {@code PUT  /trades/:id} : Updates an existing trade.
     *
     * @param id the id of the tradeDTO to save.
     * @param tradeDTO the tradeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tradeDTO,
     * or with status {@code 400 (Bad Request)} if the tradeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tradeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TradeDTO> updateTrade(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TradeDTO tradeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Trade : {}, {}", id, tradeDTO);
        if (tradeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tradeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tradeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        tradeDTO = tradeService.update(tradeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tradeDTO.getId().toString()))
            .body(tradeDTO);
    }

    /**
     * {@code PATCH  /trades/:id} : Partial updates given fields of an existing trade, field will ignore if it is null
     *
     * @param id the id of the tradeDTO to save.
     * @param tradeDTO the tradeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tradeDTO,
     * or with status {@code 400 (Bad Request)} if the tradeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tradeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tradeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TradeDTO> partialUpdateTrade(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TradeDTO tradeDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Trade partially : {}, {}", id, tradeDTO);
        if (tradeDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tradeDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tradeRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TradeDTO> result = tradeService.partialUpdate(tradeDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tradeDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /trades} : get all the trades.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of trades in body.
     */
    @GetMapping("")
    public List<TradeDTO> getAllTrades(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all Trades");
        return tradeService.findAll();
    }

    /**
     * {@code GET  /trades/:id} : get the "id" trade.
     *
     * @param id the id of the tradeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tradeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TradeDTO> getTrade(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Trade : {}", id);
        Optional<TradeDTO> tradeDTO = tradeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tradeDTO);
    }

    /**
     * {@code DELETE  /trades/:id} : delete the "id" trade.
     *
     * @param id the id of the tradeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrade(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Trade : {}", id);
        tradeService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
