package com.osttra.fx.blockstream.web.rest;

import com.osttra.fx.blockstream.domain.SmartTrade;
import com.osttra.fx.blockstream.repository.SmartTradeRepository;
import com.osttra.fx.blockstream.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.osttra.fx.blockstream.domain.SmartTrade}.
 */
@RestController
@RequestMapping("/api")
public class SmartTradeResource {

    private final Logger log = LoggerFactory.getLogger(SmartTradeResource.class);

    private static final String ENTITY_NAME = "smartTrade";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final SmartTradeRepository smartTradeRepository;

    public SmartTradeResource(SmartTradeRepository smartTradeRepository) {
        this.smartTradeRepository = smartTradeRepository;
    }

    /**
     * {@code POST  /smart-trades} : Create a new smartTrade.
     *
     * @param smartTrade the smartTrade to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new smartTrade, or with status {@code 400 (Bad Request)} if the smartTrade has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/smart-trades")
    public Mono<ResponseEntity<SmartTrade>> createSmartTrade(@RequestBody SmartTrade smartTrade) throws URISyntaxException {
        log.debug("REST request to save SmartTrade : {}", smartTrade);
        if (smartTrade.getId() != null) {
            throw new BadRequestAlertException("A new smartTrade cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return smartTradeRepository
            .save(smartTrade)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/smart-trades/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /smart-trades/:id} : Updates an existing smartTrade.
     *
     * @param id the id of the smartTrade to save.
     * @param smartTrade the smartTrade to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated smartTrade,
     * or with status {@code 400 (Bad Request)} if the smartTrade is not valid,
     * or with status {@code 500 (Internal Server Error)} if the smartTrade couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/smart-trades/{id}")
    public Mono<ResponseEntity<SmartTrade>> updateSmartTrade(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody SmartTrade smartTrade
    ) throws URISyntaxException {
        log.debug("REST request to update SmartTrade : {}, {}", id, smartTrade);
        if (smartTrade.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, smartTrade.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return smartTradeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return smartTradeRepository
                    .save(smartTrade)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /smart-trades/:id} : Partial updates given fields of an existing smartTrade, field will ignore if it is null
     *
     * @param id the id of the smartTrade to save.
     * @param smartTrade the smartTrade to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated smartTrade,
     * or with status {@code 400 (Bad Request)} if the smartTrade is not valid,
     * or with status {@code 404 (Not Found)} if the smartTrade is not found,
     * or with status {@code 500 (Internal Server Error)} if the smartTrade couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/smart-trades/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<SmartTrade>> partialUpdateSmartTrade(
        @PathVariable(value = "id", required = false) final String id,
        @RequestBody SmartTrade smartTrade
    ) throws URISyntaxException {
        log.debug("REST request to partial update SmartTrade partially : {}, {}", id, smartTrade);
        if (smartTrade.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, smartTrade.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return smartTradeRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<SmartTrade> result = smartTradeRepository
                    .findById(smartTrade.getId())
                    .map(existingSmartTrade -> {
                        if (smartTrade.getCounterParty() != null) {
                            existingSmartTrade.setCounterParty(smartTrade.getCounterParty());
                        }
                        if (smartTrade.getCurrencyBuy() != null) {
                            existingSmartTrade.setCurrencyBuy(smartTrade.getCurrencyBuy());
                        }
                        if (smartTrade.getCurrencySell() != null) {
                            existingSmartTrade.setCurrencySell(smartTrade.getCurrencySell());
                        }
                        if (smartTrade.getRate() != null) {
                            existingSmartTrade.setRate(smartTrade.getRate());
                        }
                        if (smartTrade.getAmount() != null) {
                            existingSmartTrade.setAmount(smartTrade.getAmount());
                        }
                        if (smartTrade.getContraAmount() != null) {
                            existingSmartTrade.setContraAmount(smartTrade.getContraAmount());
                        }
                        if (smartTrade.getValueDate() != null) {
                            existingSmartTrade.setValueDate(smartTrade.getValueDate());
                        }
                        if (smartTrade.getTransactionId() != null) {
                            existingSmartTrade.setTransactionId(smartTrade.getTransactionId());
                        }
                        if (smartTrade.getDirection() != null) {
                            existingSmartTrade.setDirection(smartTrade.getDirection());
                        }

                        return existingSmartTrade;
                    })
                    .flatMap(smartTradeRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /smart-trades} : get all the smartTrades.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of smartTrades in body.
     */
    @GetMapping("/smart-trades")
    public Mono<List<SmartTrade>> getAllSmartTrades() {
        log.debug("REST request to get all SmartTrades");
        return smartTradeRepository.findAll().collectList();
    }

    /**
     * {@code GET  /smart-trades} : get all the smartTrades as a stream.
     * @return the {@link Flux} of smartTrades.
     */
    @GetMapping(value = "/smart-trades", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<SmartTrade> getAllSmartTradesAsStream() {
        log.debug("REST request to get all SmartTrades as a stream");
        return smartTradeRepository.findAll();
    }

    /**
     * {@code GET  /smart-trades/:id} : get the "id" smartTrade.
     *
     * @param id the id of the smartTrade to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the smartTrade, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/smart-trades/{id}")
    public Mono<ResponseEntity<SmartTrade>> getSmartTrade(@PathVariable String id) {
        log.debug("REST request to get SmartTrade : {}", id);
        Mono<SmartTrade> smartTrade = smartTradeRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(smartTrade);
    }

    /**
     * {@code DELETE  /smart-trades/:id} : delete the "id" smartTrade.
     *
     * @param id the id of the smartTrade to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/smart-trades/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteSmartTrade(@PathVariable String id) {
        log.debug("REST request to delete SmartTrade : {}", id);
        return smartTradeRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
            );
    }
}
