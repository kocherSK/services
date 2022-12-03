package com.osttra.fx.blockstream.web.rest;

import static com.osttra.fx.blockstream.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.osttra.fx.blockstream.IntegrationTest;
import com.osttra.fx.blockstream.domain.Wallet;
import com.osttra.fx.blockstream.repository.WalletRepository;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link WalletResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class WalletResourceIT {

    private static final String DEFAULT_CURRENCY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY_CODE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_AMOUNT = new BigDecimal(1);
    private static final BigDecimal UPDATED_AMOUNT = new BigDecimal(2);

    private static final String ENTITY_API_URL = "/api/wallets";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private WalletRepository walletRepository;

    @Mock
    private WalletRepository walletRepositoryMock;

    @Autowired
    private WebTestClient webTestClient;

    private Wallet wallet;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wallet createEntity() {
        Wallet wallet = new Wallet().currencyCode(DEFAULT_CURRENCY_CODE).amount(DEFAULT_AMOUNT);
        return wallet;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Wallet createUpdatedEntity() {
        Wallet wallet = new Wallet().currencyCode(UPDATED_CURRENCY_CODE).amount(UPDATED_AMOUNT);
        return wallet;
    }

    @BeforeEach
    public void initTest() {
        walletRepository.deleteAll().block();
        wallet = createEntity();
    }

    @Test
    void createWallet() throws Exception {
        int databaseSizeBeforeCreate = walletRepository.findAll().collectList().block().size();
        // Create the Wallet
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(wallet))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll().collectList().block();
        assertThat(walletList).hasSize(databaseSizeBeforeCreate + 1);
        Wallet testWallet = walletList.get(walletList.size() - 1);
        assertThat(testWallet.getCurrencyCode()).isEqualTo(DEFAULT_CURRENCY_CODE);
        assertThat(testWallet.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
    }

    @Test
    void createWalletWithExistingId() throws Exception {
        // Create the Wallet with an existing ID
        wallet.setId("existing_id");

        int databaseSizeBeforeCreate = walletRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(wallet))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll().collectList().block();
        assertThat(walletList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllWalletsAsStream() {
        // Initialize the database
        walletRepository.save(wallet).block();

        List<Wallet> walletList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Wallet.class)
            .getResponseBody()
            .filter(wallet::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(walletList).isNotNull();
        assertThat(walletList).hasSize(1);
        Wallet testWallet = walletList.get(0);
        assertThat(testWallet.getCurrencyCode()).isEqualTo(DEFAULT_CURRENCY_CODE);
        assertThat(testWallet.getAmount()).isEqualByComparingTo(DEFAULT_AMOUNT);
    }

    @Test
    void getAllWallets() {
        // Initialize the database
        walletRepository.save(wallet).block();

        // Get all the walletList
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
            .value(hasItem(wallet.getId()))
            .jsonPath("$.[*].currencyCode")
            .value(hasItem(DEFAULT_CURRENCY_CODE))
            .jsonPath("$.[*].amount")
            .value(hasItem(sameNumber(DEFAULT_AMOUNT)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWalletsWithEagerRelationshipsIsEnabled() {
        when(walletRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(walletRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllWalletsWithEagerRelationshipsIsNotEnabled() {
        when(walletRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(walletRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getWallet() {
        // Initialize the database
        walletRepository.save(wallet).block();

        // Get the wallet
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, wallet.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(wallet.getId()))
            .jsonPath("$.currencyCode")
            .value(is(DEFAULT_CURRENCY_CODE))
            .jsonPath("$.amount")
            .value(is(sameNumber(DEFAULT_AMOUNT)));
    }

    @Test
    void getNonExistingWallet() {
        // Get the wallet
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewWallet() throws Exception {
        // Initialize the database
        walletRepository.save(wallet).block();

        int databaseSizeBeforeUpdate = walletRepository.findAll().collectList().block().size();

        // Update the wallet
        Wallet updatedWallet = walletRepository.findById(wallet.getId()).block();
        updatedWallet.currencyCode(UPDATED_CURRENCY_CODE).amount(UPDATED_AMOUNT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedWallet.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedWallet))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll().collectList().block();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
        Wallet testWallet = walletList.get(walletList.size() - 1);
        assertThat(testWallet.getCurrencyCode()).isEqualTo(UPDATED_CURRENCY_CODE);
        assertThat(testWallet.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
    }

    @Test
    void putNonExistingWallet() throws Exception {
        int databaseSizeBeforeUpdate = walletRepository.findAll().collectList().block().size();
        wallet.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, wallet.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(wallet))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll().collectList().block();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchWallet() throws Exception {
        int databaseSizeBeforeUpdate = walletRepository.findAll().collectList().block().size();
        wallet.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(wallet))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll().collectList().block();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamWallet() throws Exception {
        int databaseSizeBeforeUpdate = walletRepository.findAll().collectList().block().size();
        wallet.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(wallet))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll().collectList().block();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateWalletWithPatch() throws Exception {
        // Initialize the database
        walletRepository.save(wallet).block();

        int databaseSizeBeforeUpdate = walletRepository.findAll().collectList().block().size();

        // Update the wallet using partial update
        Wallet partialUpdatedWallet = new Wallet();
        partialUpdatedWallet.setId(wallet.getId());

        partialUpdatedWallet.currencyCode(UPDATED_CURRENCY_CODE).amount(UPDATED_AMOUNT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedWallet.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedWallet))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll().collectList().block();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
        Wallet testWallet = walletList.get(walletList.size() - 1);
        assertThat(testWallet.getCurrencyCode()).isEqualTo(UPDATED_CURRENCY_CODE);
        assertThat(testWallet.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
    }

    @Test
    void fullUpdateWalletWithPatch() throws Exception {
        // Initialize the database
        walletRepository.save(wallet).block();

        int databaseSizeBeforeUpdate = walletRepository.findAll().collectList().block().size();

        // Update the wallet using partial update
        Wallet partialUpdatedWallet = new Wallet();
        partialUpdatedWallet.setId(wallet.getId());

        partialUpdatedWallet.currencyCode(UPDATED_CURRENCY_CODE).amount(UPDATED_AMOUNT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedWallet.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedWallet))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll().collectList().block();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
        Wallet testWallet = walletList.get(walletList.size() - 1);
        assertThat(testWallet.getCurrencyCode()).isEqualTo(UPDATED_CURRENCY_CODE);
        assertThat(testWallet.getAmount()).isEqualByComparingTo(UPDATED_AMOUNT);
    }

    @Test
    void patchNonExistingWallet() throws Exception {
        int databaseSizeBeforeUpdate = walletRepository.findAll().collectList().block().size();
        wallet.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, wallet.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(wallet))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll().collectList().block();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchWallet() throws Exception {
        int databaseSizeBeforeUpdate = walletRepository.findAll().collectList().block().size();
        wallet.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(wallet))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll().collectList().block();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamWallet() throws Exception {
        int databaseSizeBeforeUpdate = walletRepository.findAll().collectList().block().size();
        wallet.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(wallet))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Wallet in the database
        List<Wallet> walletList = walletRepository.findAll().collectList().block();
        assertThat(walletList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteWallet() {
        // Initialize the database
        walletRepository.save(wallet).block();

        int databaseSizeBeforeDelete = walletRepository.findAll().collectList().block().size();

        // Delete the wallet
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, wallet.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Wallet> walletList = walletRepository.findAll().collectList().block();
        assertThat(walletList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
