package com.poke.app.web.rest;

import com.poke.app.repository.PokeUserRepository;
import com.poke.app.service.PokeUserService;
import com.poke.app.service.dto.PokeUserDTO;
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
 * REST controller for managing {@link com.poke.app.domain.PokeUser}.
 */
@RestController
@RequestMapping("/api/poke-users")
public class PokeUserResource {

    private static final Logger LOG = LoggerFactory.getLogger(PokeUserResource.class);

    private static final String ENTITY_NAME = "pokeUser";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PokeUserService pokeUserService;

    private final PokeUserRepository pokeUserRepository;

    public PokeUserResource(PokeUserService pokeUserService, PokeUserRepository pokeUserRepository) {
        this.pokeUserService = pokeUserService;
        this.pokeUserRepository = pokeUserRepository;
    }

    /**
     * {@code POST  /poke-users} : Create a new pokeUser.
     *
     * @param pokeUserDTO the pokeUserDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pokeUserDTO, or with status {@code 400 (Bad Request)} if the pokeUser has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<PokeUserDTO> createPokeUser(@Valid @RequestBody PokeUserDTO pokeUserDTO) throws URISyntaxException {
        LOG.debug("REST request to save PokeUser : {}", pokeUserDTO);
        if (pokeUserDTO.getId() != null) {
            throw new BadRequestAlertException("A new pokeUser cannot already have an ID", ENTITY_NAME, "idexists");
        }
        pokeUserDTO = pokeUserService.save(pokeUserDTO);
        return ResponseEntity.created(new URI("/api/poke-users/" + pokeUserDTO.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, pokeUserDTO.getId().toString()))
            .body(pokeUserDTO);
    }

    /**
     * {@code PUT  /poke-users/:id} : Updates an existing pokeUser.
     *
     * @param id the id of the pokeUserDTO to save.
     * @param pokeUserDTO the pokeUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pokeUserDTO,
     * or with status {@code 400 (Bad Request)} if the pokeUserDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pokeUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PokeUserDTO> updatePokeUser(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody PokeUserDTO pokeUserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update PokeUser : {}, {}", id, pokeUserDTO);
        if (pokeUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pokeUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pokeUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        pokeUserDTO = pokeUserService.update(pokeUserDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pokeUserDTO.getId().toString()))
            .body(pokeUserDTO);
    }

    /**
     * {@code PATCH  /poke-users/:id} : Partial updates given fields of an existing pokeUser, field will ignore if it is null
     *
     * @param id the id of the pokeUserDTO to save.
     * @param pokeUserDTO the pokeUserDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pokeUserDTO,
     * or with status {@code 400 (Bad Request)} if the pokeUserDTO is not valid,
     * or with status {@code 404 (Not Found)} if the pokeUserDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the pokeUserDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<PokeUserDTO> partialUpdatePokeUser(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody PokeUserDTO pokeUserDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update PokeUser partially : {}, {}", id, pokeUserDTO);
        if (pokeUserDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pokeUserDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!pokeUserRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<PokeUserDTO> result = pokeUserService.partialUpdate(pokeUserDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, pokeUserDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /poke-users} : get all the pokeUsers.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pokeUsers in body.
     */
    @GetMapping("")
    public List<PokeUserDTO> getAllPokeUsers(@RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload) {
        LOG.debug("REST request to get all PokeUsers");
        return pokeUserService.findAll();
    }

    /**
     * {@code GET  /poke-users/:id} : get the "id" pokeUser.
     *
     * @param id the id of the pokeUserDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pokeUserDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PokeUserDTO> getPokeUser(@PathVariable("id") Long id) {
        LOG.debug("REST request to get PokeUser : {}", id);
        Optional<PokeUserDTO> pokeUserDTO = pokeUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(pokeUserDTO);
    }

    /**
     * {@code DELETE  /poke-users/:id} : delete the "id" pokeUser.
     *
     * @param id the id of the pokeUserDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePokeUser(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete PokeUser : {}", id);
        pokeUserService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
