package com.poke.app.web.rest;

import com.poke.app.repository.TradeItemRepository;
import com.poke.app.service.TradeItemService;
import com.poke.app.service.dto.TradeItemDTO;
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
 * REST controller for managing {@link com.poke.app.domain.TradeItem}.
 */
@RestController
@RequestMapping("/api/trade-items")
public class TradeItemResource {

    private static final Logger LOG = LoggerFactory.getLogger(TradeItemResource.class);

    private static final String ENTITY_NAME = "tradeItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TradeItemService tradeItemService;

    private final TradeItemRepository tradeItemRepository;

    public TradeItemResource(TradeItemService tradeItemService, TradeItemRepository tradeItemRepository) {
        this.tradeItemService = tradeItemService;
        this.tradeItemRepository = tradeItemRepository;
    }

    /**
     * {@code POST  /trade-items} : Create a new tradeItem.
     *
     * @param tradeItemDTO the tradeItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tradeItemDTO, or with status {@code 400 (Bad Request)} if the tradeItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<TradeItemDTO> createTradeItem(@Valid @RequestBody TradeItemDTO tradeItemDTO) throws URISyntaxException {
        LOG.debug("REST request to save TradeItem : {}", tradeItemDTO);
        if (tradeItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new tradeItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        tradeItemDTO = tradeItemService.save(tradeItemDTO);
        return ResponseEntity.created(new URI("/api/trade-items/" + tradeItemDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, tradeItemDTO.getId().toString()))
            .body(tradeItemDTO);
    }

    /**
     * {@code PUT  /trade-items/:id} : Updates an existing tradeItem.
     *
     * @param id the id of the tradeItemDTO to save.
     * @param tradeItemDTO the tradeItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tradeItemDTO,
     * or with status {@code 400 (Bad Request)} if the tradeItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tradeItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<TradeItemDTO> updateTradeItem(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TradeItemDTO tradeItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update TradeItem : {}, {}", id, tradeItemDTO);
        if (tradeItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tradeItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tradeItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        tradeItemDTO = tradeItemService.update(tradeItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tradeItemDTO.getId().toString()))
            .body(tradeItemDTO);
    }

    /**
     * {@code PATCH  /trade-items/:id} : Partial updates given fields of an existing tradeItem, field will ignore if it is null
     *
     * @param id the id of the tradeItemDTO to save.
     * @param tradeItemDTO the tradeItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tradeItemDTO,
     * or with status {@code 400 (Bad Request)} if the tradeItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tradeItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tradeItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<TradeItemDTO> partialUpdateTradeItem(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TradeItemDTO tradeItemDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update TradeItem partially : {}, {}", id, tradeItemDTO);
        if (tradeItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tradeItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!tradeItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<TradeItemDTO> result = tradeItemService.partialUpdate(tradeItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, tradeItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /trade-items} : get all the tradeItems.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tradeItems in body.
     */
    @GetMapping("")
    public List<TradeItemDTO> getAllTradeItems(
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get all TradeItems");
        return tradeItemService.findAll();
    }

    /**
     * {@code GET  /trade-items/:id} : get the "id" tradeItem.
     *
     * @param id the id of the tradeItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tradeItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<TradeItemDTO> getTradeItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to get TradeItem : {}", id);
        Optional<TradeItemDTO> tradeItemDTO = tradeItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tradeItemDTO);
    }

    /**
     * {@code DELETE  /trade-items/:id} : delete the "id" tradeItem.
     *
     * @param id the id of the tradeItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTradeItem(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete TradeItem : {}", id);
        tradeItemService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
