package com.osttra.fx.blockstream.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.osttra.fx.blockstream.IntegrationTest;
import com.osttra.fx.blockstream.domain.Currencies;
import com.osttra.fx.blockstream.repository.CurrenciesRepository;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link CurrenciesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CurrenciesResourceIT {

    private static final String DEFAULT_CURRENCY_NAME = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENCY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY_CODE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/currencies";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private CurrenciesRepository currenciesRepository;

    @Autowired
    private WebTestClient webTestClient;

    private Currencies currencies;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Currencies createEntity() {
        Currencies currencies = new Currencies().currencyName(DEFAULT_CURRENCY_NAME).currencyCode(DEFAULT_CURRENCY_CODE);
        return currencies;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Currencies createUpdatedEntity() {
        Currencies currencies = new Currencies().currencyName(UPDATED_CURRENCY_NAME).currencyCode(UPDATED_CURRENCY_CODE);
        return currencies;
    }

    @BeforeEach
    public void initTest() {
        currenciesRepository.deleteAll().block();
        currencies = createEntity();
    }

    @Test
    void createCurrencies() throws Exception {
        int databaseSizeBeforeCreate = currenciesRepository.findAll().collectList().block().size();
        // Create the Currencies
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(currencies))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Currencies in the database
        List<Currencies> currenciesList = currenciesRepository.findAll().collectList().block();
        assertThat(currenciesList).hasSize(databaseSizeBeforeCreate + 1);
        Currencies testCurrencies = currenciesList.get(currenciesList.size() - 1);
        assertThat(testCurrencies.getCurrencyName()).isEqualTo(DEFAULT_CURRENCY_NAME);
        assertThat(testCurrencies.getCurrencyCode()).isEqualTo(DEFAULT_CURRENCY_CODE);
    }

    @Test
    void createCurrenciesWithExistingId() throws Exception {
        // Create the Currencies with an existing ID
        currencies.setId("existing_id");

        int databaseSizeBeforeCreate = currenciesRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(currencies))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Currencies in the database
        List<Currencies> currenciesList = currenciesRepository.findAll().collectList().block();
        assertThat(currenciesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCurrenciesAsStream() {
        // Initialize the database
        currenciesRepository.save(currencies).block();

        List<Currencies> currenciesList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Currencies.class)
            .getResponseBody()
            .filter(currencies::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(currenciesList).isNotNull();
        assertThat(currenciesList).hasSize(1);
        Currencies testCurrencies = currenciesList.get(0);
        assertThat(testCurrencies.getCurrencyName()).isEqualTo(DEFAULT_CURRENCY_NAME);
        assertThat(testCurrencies.getCurrencyCode()).isEqualTo(DEFAULT_CURRENCY_CODE);
    }

    @Test
    void getAllCurrencies() {
        // Initialize the database
        currenciesRepository.save(currencies).block();

        // Get all the currenciesList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(currencies.getId()))
            .jsonPath("$.[*].currencyName")
            .value(hasItem(DEFAULT_CURRENCY_NAME))
            .jsonPath("$.[*].currencyCode")
            .value(hasItem(DEFAULT_CURRENCY_CODE));
    }

    @Test
    void getCurrencies() {
        // Initialize the database
        currenciesRepository.save(currencies).block();

        // Get the currencies
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, currencies.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(currencies.getId()))
            .jsonPath("$.currencyName")
            .value(is(DEFAULT_CURRENCY_NAME))
            .jsonPath("$.currencyCode")
            .value(is(DEFAULT_CURRENCY_CODE));
    }

    @Test
    void getNonExistingCurrencies() {
        // Get the currencies
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCurrencies() throws Exception {
        // Initialize the database
        currenciesRepository.save(currencies).block();

        int databaseSizeBeforeUpdate = currenciesRepository.findAll().collectList().block().size();

        // Update the currencies
        Currencies updatedCurrencies = currenciesRepository.findById(currencies.getId()).block();
        updatedCurrencies.currencyName(UPDATED_CURRENCY_NAME).currencyCode(UPDATED_CURRENCY_CODE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCurrencies.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCurrencies))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Currencies in the database
        List<Currencies> currenciesList = currenciesRepository.findAll().collectList().block();
        assertThat(currenciesList).hasSize(databaseSizeBeforeUpdate);
        Currencies testCurrencies = currenciesList.get(currenciesList.size() - 1);
        assertThat(testCurrencies.getCurrencyName()).isEqualTo(UPDATED_CURRENCY_NAME);
        assertThat(testCurrencies.getCurrencyCode()).isEqualTo(UPDATED_CURRENCY_CODE);
    }

    @Test
    void putNonExistingCurrencies() throws Exception {
        int databaseSizeBeforeUpdate = currenciesRepository.findAll().collectList().block().size();
        currencies.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, currencies.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(currencies))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Currencies in the database
        List<Currencies> currenciesList = currenciesRepository.findAll().collectList().block();
        assertThat(currenciesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCurrencies() throws Exception {
        int databaseSizeBeforeUpdate = currenciesRepository.findAll().collectList().block().size();
        currencies.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(currencies))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Currencies in the database
        List<Currencies> currenciesList = currenciesRepository.findAll().collectList().block();
        assertThat(currenciesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCurrencies() throws Exception {
        int databaseSizeBeforeUpdate = currenciesRepository.findAll().collectList().block().size();
        currencies.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(currencies))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Currencies in the database
        List<Currencies> currenciesList = currenciesRepository.findAll().collectList().block();
        assertThat(currenciesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCurrenciesWithPatch() throws Exception {
        // Initialize the database
        currenciesRepository.save(currencies).block();

        int databaseSizeBeforeUpdate = currenciesRepository.findAll().collectList().block().size();

        // Update the currencies using partial update
        Currencies partialUpdatedCurrencies = new Currencies();
        partialUpdatedCurrencies.setId(currencies.getId());

        partialUpdatedCurrencies.currencyCode(UPDATED_CURRENCY_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCurrencies.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCurrencies))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Currencies in the database
        List<Currencies> currenciesList = currenciesRepository.findAll().collectList().block();
        assertThat(currenciesList).hasSize(databaseSizeBeforeUpdate);
        Currencies testCurrencies = currenciesList.get(currenciesList.size() - 1);
        assertThat(testCurrencies.getCurrencyName()).isEqualTo(DEFAULT_CURRENCY_NAME);
        assertThat(testCurrencies.getCurrencyCode()).isEqualTo(UPDATED_CURRENCY_CODE);
    }

    @Test
    void fullUpdateCurrenciesWithPatch() throws Exception {
        // Initialize the database
        currenciesRepository.save(currencies).block();

        int databaseSizeBeforeUpdate = currenciesRepository.findAll().collectList().block().size();

        // Update the currencies using partial update
        Currencies partialUpdatedCurrencies = new Currencies();
        partialUpdatedCurrencies.setId(currencies.getId());

        partialUpdatedCurrencies.currencyName(UPDATED_CURRENCY_NAME).currencyCode(UPDATED_CURRENCY_CODE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCurrencies.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCurrencies))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Currencies in the database
        List<Currencies> currenciesList = currenciesRepository.findAll().collectList().block();
        assertThat(currenciesList).hasSize(databaseSizeBeforeUpdate);
        Currencies testCurrencies = currenciesList.get(currenciesList.size() - 1);
        assertThat(testCurrencies.getCurrencyName()).isEqualTo(UPDATED_CURRENCY_NAME);
        assertThat(testCurrencies.getCurrencyCode()).isEqualTo(UPDATED_CURRENCY_CODE);
    }

    @Test
    void patchNonExistingCurrencies() throws Exception {
        int databaseSizeBeforeUpdate = currenciesRepository.findAll().collectList().block().size();
        currencies.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, currencies.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(currencies))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Currencies in the database
        List<Currencies> currenciesList = currenciesRepository.findAll().collectList().block();
        assertThat(currenciesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCurrencies() throws Exception {
        int databaseSizeBeforeUpdate = currenciesRepository.findAll().collectList().block().size();
        currencies.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(currencies))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Currencies in the database
        List<Currencies> currenciesList = currenciesRepository.findAll().collectList().block();
        assertThat(currenciesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCurrencies() throws Exception {
        int databaseSizeBeforeUpdate = currenciesRepository.findAll().collectList().block().size();
        currencies.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(currencies))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Currencies in the database
        List<Currencies> currenciesList = currenciesRepository.findAll().collectList().block();
        assertThat(currenciesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCurrencies() {
        // Initialize the database
        currenciesRepository.save(currencies).block();

        int databaseSizeBeforeDelete = currenciesRepository.findAll().collectList().block().size();

        // Delete the currencies
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, currencies.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Currencies> currenciesList = currenciesRepository.findAll().collectList().block();
        assertThat(currenciesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
