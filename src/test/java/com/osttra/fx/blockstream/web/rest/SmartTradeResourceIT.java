package com.osttra.fx.blockstream.web.rest;

import static com.osttra.fx.blockstream.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.osttra.fx.blockstream.IntegrationTest;
import com.osttra.fx.blockstream.domain.SmartTrade;
import com.osttra.fx.blockstream.repository.SmartTradeRepository;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link SmartTradeResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SmartTradeResourceIT {

    private static final String DEFAULT_COUNTER_PARTY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTER_PARTY = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENCY_BUY = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY_BUY = "BBBBBBBBBB";

    private static final String DEFAULT_CURRENCY_SELL = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY_SELL = "BBBBBBBBBB";

    private static final Double DEFAULT_RATE = 1D;
    private static final Double UPDATED_RATE = 2D;

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final BigDecimal DEFAULT_CONTRA_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_CONTRA_AMOUNT = new BigDecimal(2);

    private static final LocalDate DEFAULT_VALUE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_VALUE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_TRANSACTION_ID = "AAAAAAAAAA";
    private static final String UPDATED_TRANSACTION_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/smart-trades";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private SmartTradeRepository smartTradeRepository;

    @Autowired
    private WebTestClient webTestClient;

    private SmartTrade smartTrade;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SmartTrade createEntity() {
        SmartTrade smartTrade = new SmartTrade()
            .counterParty(DEFAULT_COUNTER_PARTY)
            .currencyBuy(DEFAULT_CURRENCY_BUY)
            .currencySell(DEFAULT_CURRENCY_SELL)
            .rate(DEFAULT_RATE)
            .amount(DEFAULT_AMOUNT)
            .contraAmount(DEFAULT_CONTRA_AMOUNT)
            .valueDate(DEFAULT_VALUE_DATE)
            .transactionId(DEFAULT_TRANSACTION_ID);
        return smartTrade;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static SmartTrade createUpdatedEntity() {
        SmartTrade smartTrade = new SmartTrade()
            .counterParty(UPDATED_COUNTER_PARTY)
            .currencyBuy(UPDATED_CURRENCY_BUY)
            .currencySell(UPDATED_CURRENCY_SELL)
            .rate(UPDATED_RATE)
            .amount(UPDATED_AMOUNT)
            .contraAmount(UPDATED_CONTRA_AMOUNT)
            .valueDate(UPDATED_VALUE_DATE)
            .transactionId(UPDATED_TRANSACTION_ID);
        return smartTrade;
    }

    @BeforeEach
    public void initTest() {
        smartTradeRepository.deleteAll().block();
        smartTrade = createEntity();
    }

    @Test
    void createSmartTrade() throws Exception {
        int databaseSizeBeforeCreate = smartTradeRepository.findAll().collectList().block().size();
        // Create the SmartTrade
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(smartTrade))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the SmartTrade in the database
        List<SmartTrade> smartTradeList = smartTradeRepository.findAll().collectList().block();
        assertThat(smartTradeList).hasSize(databaseSizeBeforeCreate + 1);
        SmartTrade testSmartTrade = smartTradeList.get(smartTradeList.size() - 1);
        assertThat(testSmartTrade.getCounterParty()).isEqualTo(DEFAULT_COUNTER_PARTY);
        assertThat(testSmartTrade.getCurrencyBuy()).isEqualTo(DEFAULT_CURRENCY_BUY);
        assertThat(testSmartTrade.getCurrencySell()).isEqualTo(DEFAULT_CURRENCY_SELL);
        assertThat(testSmartTrade.getRate()).isEqualTo(DEFAULT_RATE);
        assertThat(testSmartTrade.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testSmartTrade.getContraAmount()).isEqualByComparingTo(DEFAULT_CONTRA_AMOUNT);
        assertThat(testSmartTrade.getValueDate()).isEqualTo(DEFAULT_VALUE_DATE);
        assertThat(testSmartTrade.getTransactionId()).isEqualTo(DEFAULT_TRANSACTION_ID);
    }

    @Test
    void createSmartTradeWithExistingId() throws Exception {
        // Create the SmartTrade with an existing ID
        smartTrade.setId("existing_id");

        int databaseSizeBeforeCreate = smartTradeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(smartTrade))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SmartTrade in the database
        List<SmartTrade> smartTradeList = smartTradeRepository.findAll().collectList().block();
        assertThat(smartTradeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllSmartTradesAsStream() {
        // Initialize the database
        smartTradeRepository.save(smartTrade).block();

        List<SmartTrade> smartTradeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(SmartTrade.class)
            .getResponseBody()
            .filter(smartTrade::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(smartTradeList).isNotNull();
        assertThat(smartTradeList).hasSize(1);
        SmartTrade testSmartTrade = smartTradeList.get(0);
        assertThat(testSmartTrade.getCounterParty()).isEqualTo(DEFAULT_COUNTER_PARTY);
        assertThat(testSmartTrade.getCurrencyBuy()).isEqualTo(DEFAULT_CURRENCY_BUY);
        assertThat(testSmartTrade.getCurrencySell()).isEqualTo(DEFAULT_CURRENCY_SELL);
        assertThat(testSmartTrade.getRate()).isEqualTo(DEFAULT_RATE);
        assertThat(testSmartTrade.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
        assertThat(testSmartTrade.getContraAmount()).isEqualByComparingTo(DEFAULT_CONTRA_AMOUNT);
        assertThat(testSmartTrade.getValueDate()).isEqualTo(DEFAULT_VALUE_DATE);
        assertThat(testSmartTrade.getTransactionId()).isEqualTo(DEFAULT_TRANSACTION_ID);
    }

    @Test
    void getAllSmartTrades() {
        // Initialize the database
        smartTradeRepository.save(smartTrade).block();

        // Get all the smartTradeList
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
            .value(hasItem(smartTrade.getId()))
            .jsonPath("$.[*].counterParty")
            .value(hasItem(DEFAULT_COUNTER_PARTY))
            .jsonPath("$.[*].currencyBuy")
            .value(hasItem(DEFAULT_CURRENCY_BUY))
            .jsonPath("$.[*].currencySell")
            .value(hasItem(DEFAULT_CURRENCY_SELL))
            .jsonPath("$.[*].rate")
            .value(hasItem(DEFAULT_RATE.doubleValue()))
            .jsonPath("$.[*].amount")
            .value(hasItem(sameNumber(DEFAULT_AMOUNT)))
            .jsonPath("$.[*].contraAmount")
            .value(hasItem(sameNumber(DEFAULT_CONTRA_AMOUNT)))
            .jsonPath("$.[*].valueDate")
            .value(hasItem(DEFAULT_VALUE_DATE.toString()))
            .jsonPath("$.[*].transactionId")
            .value(hasItem(DEFAULT_TRANSACTION_ID));
    }

    @Test
    void getSmartTrade() {
        // Initialize the database
        smartTradeRepository.save(smartTrade).block();

        // Get the smartTrade
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, smartTrade.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(smartTrade.getId()))
            .jsonPath("$.counterParty")
            .value(is(DEFAULT_COUNTER_PARTY))
            .jsonPath("$.currencyBuy")
            .value(is(DEFAULT_CURRENCY_BUY))
            .jsonPath("$.currencySell")
            .value(is(DEFAULT_CURRENCY_SELL))
            .jsonPath("$.rate")
            .value(is(DEFAULT_RATE.doubleValue()))
            .jsonPath("$.amount")
            .value(is(sameNumber(DEFAULT_AMOUNT)))
            .jsonPath("$.contraAmount")
            .value(is(sameNumber(DEFAULT_CONTRA_AMOUNT)))
            .jsonPath("$.valueDate")
            .value(is(DEFAULT_VALUE_DATE.toString()))
            .jsonPath("$.transactionId")
            .value(is(DEFAULT_TRANSACTION_ID));
    }

    @Test
    void getNonExistingSmartTrade() {
        // Get the smartTrade
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewSmartTrade() throws Exception {
        // Initialize the database
        smartTradeRepository.save(smartTrade).block();

        int databaseSizeBeforeUpdate = smartTradeRepository.findAll().collectList().block().size();

        // Update the smartTrade
        SmartTrade updatedSmartTrade = smartTradeRepository.findById(smartTrade.getId()).block();
        updatedSmartTrade
            .counterParty(UPDATED_COUNTER_PARTY)
            .currencyBuy(UPDATED_CURRENCY_BUY)
            .currencySell(UPDATED_CURRENCY_SELL)
            .rate(UPDATED_RATE)
            .amount(UPDATED_AMOUNT)
            .contraAmount(UPDATED_CONTRA_AMOUNT)
            .valueDate(UPDATED_VALUE_DATE)
            .transactionId(UPDATED_TRANSACTION_ID);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedSmartTrade.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedSmartTrade))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SmartTrade in the database
        List<SmartTrade> smartTradeList = smartTradeRepository.findAll().collectList().block();
        assertThat(smartTradeList).hasSize(databaseSizeBeforeUpdate);
        SmartTrade testSmartTrade = smartTradeList.get(smartTradeList.size() - 1);
        assertThat(testSmartTrade.getCounterParty()).isEqualTo(UPDATED_COUNTER_PARTY);
        assertThat(testSmartTrade.getCurrencyBuy()).isEqualTo(UPDATED_CURRENCY_BUY);
        assertThat(testSmartTrade.getCurrencySell()).isEqualTo(UPDATED_CURRENCY_SELL);
        assertThat(testSmartTrade.getRate()).isEqualTo(UPDATED_RATE);
        assertThat(testSmartTrade.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testSmartTrade.getContraAmount()).isEqualByComparingTo(UPDATED_CONTRA_AMOUNT);
        assertThat(testSmartTrade.getValueDate()).isEqualTo(UPDATED_VALUE_DATE);
        assertThat(testSmartTrade.getTransactionId()).isEqualTo(UPDATED_TRANSACTION_ID);
    }

    @Test
    void putNonExistingSmartTrade() throws Exception {
        int databaseSizeBeforeUpdate = smartTradeRepository.findAll().collectList().block().size();
        smartTrade.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, smartTrade.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(smartTrade))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SmartTrade in the database
        List<SmartTrade> smartTradeList = smartTradeRepository.findAll().collectList().block();
        assertThat(smartTradeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSmartTrade() throws Exception {
        int databaseSizeBeforeUpdate = smartTradeRepository.findAll().collectList().block().size();
        smartTrade.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(smartTrade))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SmartTrade in the database
        List<SmartTrade> smartTradeList = smartTradeRepository.findAll().collectList().block();
        assertThat(smartTradeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSmartTrade() throws Exception {
        int databaseSizeBeforeUpdate = smartTradeRepository.findAll().collectList().block().size();
        smartTrade.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(smartTrade))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SmartTrade in the database
        List<SmartTrade> smartTradeList = smartTradeRepository.findAll().collectList().block();
        assertThat(smartTradeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSmartTradeWithPatch() throws Exception {
        // Initialize the database
        smartTradeRepository.save(smartTrade).block();

        int databaseSizeBeforeUpdate = smartTradeRepository.findAll().collectList().block().size();

        // Update the smartTrade using partial update
        SmartTrade partialUpdatedSmartTrade = new SmartTrade();
        partialUpdatedSmartTrade.setId(smartTrade.getId());

        partialUpdatedSmartTrade.rate(UPDATED_RATE).amount(UPDATED_AMOUNT).contraAmount(UPDATED_CONTRA_AMOUNT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSmartTrade.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSmartTrade))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SmartTrade in the database
        List<SmartTrade> smartTradeList = smartTradeRepository.findAll().collectList().block();
        assertThat(smartTradeList).hasSize(databaseSizeBeforeUpdate);
        SmartTrade testSmartTrade = smartTradeList.get(smartTradeList.size() - 1);
        assertThat(testSmartTrade.getCounterParty()).isEqualTo(DEFAULT_COUNTER_PARTY);
        assertThat(testSmartTrade.getCurrencyBuy()).isEqualTo(DEFAULT_CURRENCY_BUY);
        assertThat(testSmartTrade.getCurrencySell()).isEqualTo(DEFAULT_CURRENCY_SELL);
        assertThat(testSmartTrade.getRate()).isEqualTo(UPDATED_RATE);
        assertThat(testSmartTrade.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testSmartTrade.getContraAmount()).isEqualByComparingTo(UPDATED_CONTRA_AMOUNT);
        assertThat(testSmartTrade.getValueDate()).isEqualTo(DEFAULT_VALUE_DATE);
        assertThat(testSmartTrade.getTransactionId()).isEqualTo(DEFAULT_TRANSACTION_ID);
    }

    @Test
    void fullUpdateSmartTradeWithPatch() throws Exception {
        // Initialize the database
        smartTradeRepository.save(smartTrade).block();

        int databaseSizeBeforeUpdate = smartTradeRepository.findAll().collectList().block().size();

        // Update the smartTrade using partial update
        SmartTrade partialUpdatedSmartTrade = new SmartTrade();
        partialUpdatedSmartTrade.setId(smartTrade.getId());

        partialUpdatedSmartTrade
            .counterParty(UPDATED_COUNTER_PARTY)
            .currencyBuy(UPDATED_CURRENCY_BUY)
            .currencySell(UPDATED_CURRENCY_SELL)
            .rate(UPDATED_RATE)
            .amount(UPDATED_AMOUNT)
            .contraAmount(UPDATED_CONTRA_AMOUNT)
            .valueDate(UPDATED_VALUE_DATE)
            .transactionId(UPDATED_TRANSACTION_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSmartTrade.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSmartTrade))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the SmartTrade in the database
        List<SmartTrade> smartTradeList = smartTradeRepository.findAll().collectList().block();
        assertThat(smartTradeList).hasSize(databaseSizeBeforeUpdate);
        SmartTrade testSmartTrade = smartTradeList.get(smartTradeList.size() - 1);
        assertThat(testSmartTrade.getCounterParty()).isEqualTo(UPDATED_COUNTER_PARTY);
        assertThat(testSmartTrade.getCurrencyBuy()).isEqualTo(UPDATED_CURRENCY_BUY);
        assertThat(testSmartTrade.getCurrencySell()).isEqualTo(UPDATED_CURRENCY_SELL);
        assertThat(testSmartTrade.getRate()).isEqualTo(UPDATED_RATE);
        assertThat(testSmartTrade.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
        assertThat(testSmartTrade.getContraAmount()).isEqualByComparingTo(UPDATED_CONTRA_AMOUNT);
        assertThat(testSmartTrade.getValueDate()).isEqualTo(UPDATED_VALUE_DATE);
        assertThat(testSmartTrade.getTransactionId()).isEqualTo(UPDATED_TRANSACTION_ID);
    }

    @Test
    void patchNonExistingSmartTrade() throws Exception {
        int databaseSizeBeforeUpdate = smartTradeRepository.findAll().collectList().block().size();
        smartTrade.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, smartTrade.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(smartTrade))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SmartTrade in the database
        List<SmartTrade> smartTradeList = smartTradeRepository.findAll().collectList().block();
        assertThat(smartTradeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSmartTrade() throws Exception {
        int databaseSizeBeforeUpdate = smartTradeRepository.findAll().collectList().block().size();
        smartTrade.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(smartTrade))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the SmartTrade in the database
        List<SmartTrade> smartTradeList = smartTradeRepository.findAll().collectList().block();
        assertThat(smartTradeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSmartTrade() throws Exception {
        int databaseSizeBeforeUpdate = smartTradeRepository.findAll().collectList().block().size();
        smartTrade.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(smartTrade))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the SmartTrade in the database
        List<SmartTrade> smartTradeList = smartTradeRepository.findAll().collectList().block();
        assertThat(smartTradeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSmartTrade() {
        // Initialize the database
        smartTradeRepository.save(smartTrade).block();

        int databaseSizeBeforeDelete = smartTradeRepository.findAll().collectList().block().size();

        // Delete the smartTrade
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, smartTrade.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<SmartTrade> smartTradeList = smartTradeRepository.findAll().collectList().block();
        assertThat(smartTradeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
