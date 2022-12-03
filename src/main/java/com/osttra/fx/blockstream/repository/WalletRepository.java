package com.osttra.fx.blockstream.repository;

import com.osttra.fx.blockstream.domain.Wallet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data MongoDB reactive repository for the Wallet entity.
 */
@SuppressWarnings("unused")
@Repository
public interface WalletRepository extends ReactiveMongoRepository<Wallet, String> {
    @Query("{}")
    Flux<Wallet> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    Flux<Wallet> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Mono<Wallet> findOneWithEagerRelationships(String id);
}
