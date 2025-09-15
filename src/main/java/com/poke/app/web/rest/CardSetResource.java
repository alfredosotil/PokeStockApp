package com.poke.app.web.rest;

import com.poke.app.repository.CardSetRepository;
import com.poke.app.service.CardSetService;
import com.poke.app.service.dto.CardSetDTO;
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
 * REST controller for managing {@link com.poke.app.domain.CardSet}.
 */
@RestController
@RequestMapping("/api/card-sets")
public class CardSetResource {

    private static final Logger LOG = LoggerFactory.getLogger(CardSetResource.class);

    private static final String ENTITY_NAME = "cardSet";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CardSetService cardSetService;

    private final CardSetRepository cardSetRepository;

    public CardSetResource(CardSetService cardSetService, CardSetRepository cardSetRepository) {
        this.cardSetService = cardSetService;
        this.cardSetRepository = cardSetRepository;
    }

    /**
     * {@code POST  /card-sets} : Create a new cardSet.
     *
     * @param cardSetDTO the cardSetDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new cardSetDTO, or with status {@code 400 (Bad Request)} if the cardSet has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<CardSetDTO> createCardSet(@Valid @RequestBody CardSetDTO cardSetDTO) throws URISyntaxException {
        LOG.debug("REST request to save CardSet : {}", cardSetDTO);
        if (cardSetDTO.getId() != null) {
            throw new BadRequestAlertException("A new cardSet cannot already have an ID", ENTITY_NAME, "idexists");
        }
        cardSetDTO = cardSetService.save(cardSetDTO);
        return ResponseEntity.created(new URI("/api/card-sets/" + cardSetDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, cardSetDTO.getId().toString()))
            .body(cardSetDTO);
    }

    /**
     * {@code PUT  /card-sets/:id} : Updates an existing cardSet.
     *
     * @param id the id of the cardSetDTO to save.
     * @param cardSetDTO the cardSetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cardSetDTO,
     * or with status {@code 400 (Bad Request)} if the cardSetDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the cardSetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CardSetDTO> updateCardSet(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody CardSetDTO cardSetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update CardSet : {}, {}", id, cardSetDTO);
        if (cardSetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cardSetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cardSetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        cardSetDTO = cardSetService.update(cardSetDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cardSetDTO.getId().toString()))
            .body(cardSetDTO);
    }

    /**
     * {@code PATCH  /card-sets/:id} : Partial updates given fields of an existing cardSet, field will ignore if it is null
     *
     * @param id the id of the cardSetDTO to save.
     * @param cardSetDTO the cardSetDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated cardSetDTO,
     * or with status {@code 400 (Bad Request)} if the cardSetDTO is not valid,
     * or with status {@code 404 (Not Found)} if the cardSetDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the cardSetDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CardSetDTO> partialUpdateCardSet(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody CardSetDTO cardSetDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update CardSet partially : {}, {}", id, cardSetDTO);
        if (cardSetDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, cardSetDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cardSetRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CardSetDTO> result = cardSetService.partialUpdate(cardSetDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, cardSetDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /card-sets} : get all the cardSets.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cardSets in body.
     */
    @GetMapping("")
    public ResponseEntity<List<CardSetDTO>> getAllCardSets(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of CardSets");
        Page<CardSetDTO> page = cardSetService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /card-sets/:id} : get the "id" cardSet.
     *
     * @param id the id of the cardSetDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the cardSetDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CardSetDTO> getCardSet(@PathVariable("id") Long id) {
        LOG.debug("REST request to get CardSet : {}", id);
        Optional<CardSetDTO> cardSetDTO = cardSetService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cardSetDTO);
    }

    /**
     * {@code DELETE  /card-sets/:id} : delete the "id" cardSet.
     *
     * @param id the id of the cardSetDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCardSet(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete CardSet : {}", id);
        cardSetService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
